package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitClaim;
import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitIdempotencyStore;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelCommitResult;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * S-213 엑셀 업서트 확정(commit)의 요청 레벨 중복 처리를 막는 멱등성 저장소.
 * <p>
 * claim()의 흐름:
 * 1) INSERT ... ON CONFLICT DO NOTHING으로 "처음 보는 commitId"를 원자적으로 판별한다
 *    (AssetCodeSequencerImpl과 같은 패턴 - Postgres가 충돌 대상 행에 락을 걸어 동시 요청을 직렬화).
 * 2) 이미 있는 행이면 request_hash로 "정말 같은 내용의 요청인지"를 확인한다 - 다르면 거부(REQUEST_MISMATCH).
 * 3) 같은 내용인데 이미 COMPLETED면 그 결과를 그대로 재사용(ALREADY_COMPLETED).
 * 4) 같은 내용인데 FAILED이거나 IN_PROGRESS인데 lease가 만료됐으면(처리 프로세스가 죽은 것으로
 *    간주) 조건부 UPDATE로 재claim을 시도한다 - 성공하면 이번 호출자가 처리(CLAIMED), 실패하면
 *    (즉 실제로는 아직 유효한 lease를 가진 IN_PROGRESS였으면) 다른 시도가 지금 처리 중인 것이므로
 *    거부(IN_PROGRESS_ELSEWHERE).
 * <p>
 * 여러 인스턴스·재시작을 고려해 애플리케이션 메모리가 아니라 DB에 기록하므로, 어느 인스턴스가
 * 요청을 받아도 동일하게 동작한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelCommitIdempotencyStoreImpl implements ExcelCommitIdempotencyStore {

    /** IN_PROGRESS 상태의 유효 기간 - 이보다 오래 처리 중이면 프로세스가 죽은 것으로 보고 재claim을 허용한다.
     * 최대 2000행(MAX_UPLOAD_ROWS)을 순차 처리하는 시간을 넉넉히 포함하되, 대량 배치가 이 시간을
     * 넘길 정도로 오래 걸리는 경우를 대비한 lease 자동 연장은 이번 범위에서는 구현하지 않았다
     * (문서화된 한계 - docs/운영/엑셀_업서트_멱등성.md 참고). */
    private static final String LEASE_DURATION_SQL = "interval '10 minutes'";

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private record ExistingRequest(String status, String requestHash, Integer createdCount, Integer updatedCount,
                                    Integer skippedCount, String failedRowsJson) {
    }

    private static final RowMapper<ExistingRequest> EXISTING_ROW_MAPPER = (rs, rowNum) -> new ExistingRequest(
            rs.getString("status"),
            rs.getString("request_hash"),
            rs.getObject("created_count", Integer.class),
            rs.getObject("updated_count", Integer.class),
            rs.getObject("skipped_count", Integer.class),
            rs.getString("failed_rows_json")
    );

    @Override
    public ExcelCommitClaim claim(UUID commitId, UUID requestedBy, String requestHash) {
        int inserted = jdbcTemplate.update(
                "INSERT INTO excel_commit_request (commit_id, requested_by, request_hash, status, lease_expires_at, attempt_count, create_at, update_at) " +
                        "VALUES (?, ?, ?, 'IN_PROGRESS', now() + " + LEASE_DURATION_SQL + ", 1, now(), now()) " +
                        "ON CONFLICT (commit_id) DO NOTHING",
                commitId, requestedBy, requestHash);
        if (inserted == 1) {
            return ExcelCommitClaim.claimed();
        }

        List<ExistingRequest> found = jdbcTemplate.query(
                "SELECT status, request_hash, created_count, updated_count, skipped_count, failed_rows_json " +
                        "FROM excel_commit_request WHERE commit_id = ?",
                EXISTING_ROW_MAPPER, commitId);
        if (found.isEmpty()) {
            // claim의 INSERT가 방금 막 이 순간 사라진 행과 충돌했을 리는 없지만(행은 삭제되지 않음),
            // 방어적으로 처리 중 오류로 간주한다.
            throw new IllegalStatusException("엑셀 확정 요청 상태를 확인할 수 없습니다.");
        }

        ExistingRequest existing = found.get(0);
        if (!existing.requestHash().equals(requestHash)) {
            return ExcelCommitClaim.requestMismatch();
        }

        if ("COMPLETED".equals(existing.status())) {
            return ExcelCommitClaim.alreadyCompleted(toResult(existing));
        }

        int reclaimed = jdbcTemplate.update(
                "UPDATE excel_commit_request " +
                        "SET status = 'IN_PROGRESS', lease_expires_at = now() + " + LEASE_DURATION_SQL + ", " +
                        "    attempt_count = attempt_count + 1, last_error = NULL, update_at = now() " +
                        "WHERE commit_id = ? AND request_hash = ? " +
                        "  AND (status = 'FAILED' OR (status = 'IN_PROGRESS' AND lease_expires_at <= now()))",
                commitId, requestHash);

        return reclaimed == 1 ? ExcelCommitClaim.claimed() : ExcelCommitClaim.inProgressElsewhere();
    }

    @Override
    public void complete(UUID commitId, TangibleAssetExcelCommitResult result) {
        String failedJson;
        try {
            failedJson = objectMapper.writeValueAsString(result.getFailedRows());
        } catch (Exception e) {
            log.error("엑셀 업서트 확정 결과 직렬화 실패 (commitId={})", commitId, e);
            failedJson = "[]";
        }

        jdbcTemplate.update(
                "UPDATE excel_commit_request " +
                        "SET status = 'COMPLETED', created_count = ?, updated_count = ?, skipped_count = ?, " +
                        "    failed_rows_json = ?, last_error = NULL, update_at = now() " +
                        "WHERE commit_id = ?",
                result.getCreatedCount(), result.getUpdatedCount(), result.getSkippedCount(), failedJson, commitId);
    }

    @Override
    public void fail(UUID commitId, String errorMessage) {
        jdbcTemplate.update(
                "UPDATE excel_commit_request SET status = 'FAILED', last_error = ?, update_at = now() WHERE commit_id = ?",
                errorMessage, commitId);
    }

    private TangibleAssetExcelCommitResult toResult(ExistingRequest existing) {
        return TangibleAssetExcelCommitResult.builder()
                .createdCount(existing.createdCount() == null ? 0 : existing.createdCount())
                .updatedCount(existing.updatedCount() == null ? 0 : existing.updatedCount())
                .skippedCount(existing.skippedCount() == null ? 0 : existing.skippedCount())
                .failedRows(deserializeFailedRows(existing.failedRowsJson()))
                .build();
    }

    private List<TangibleAssetExcelRow> deserializeFailedRows(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<TangibleAssetExcelRow>>() {
            });
        } catch (Exception e) {
            log.error("엑셀 업서트 확정 결과 역직렬화 실패", e);
            throw new IllegalStatusException("이전 확정 결과를 불러오는 중 오류가 발생했습니다.");
        }
    }
}
