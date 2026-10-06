package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitRowResult;
import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitRowStore;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * S-213 엑셀 확정의 행(row) 단위 멱등성 저장소. commit_id + row_key를 기본키로 써서, 같은 commitId를
 * 재시도해도 이미 COMPLETED/FAILED로 끝난 행은 다시 처리하지 않는다.
 * <p>
 * claimRow()는 "처음 보는 행" 또는 "이전 시도가 죽어 IN_PROGRESS로 남은 행"만 새로 claim한다 -
 * 이미 COMPLETED/FAILED로 끝난 행은 절대 덮어쓰지 않는다(WHERE절로 조건부 UPSERT).
 */
@Component
@RequiredArgsConstructor
public class ExcelCommitRowStoreImpl implements ExcelCommitRowStore {

    private static final RowMapper<ExcelCommitRowResult> ROW_MAPPER = (rs, rowNum) -> ExcelCommitRowResult.builder()
            .status(rs.getString("status"))
            .resultAction(rs.getString("result_action"))
            .tangibleAssetId((UUID) rs.getObject("tangible_asset_id"))
            .errorMessage(rs.getString("error_message"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<ExcelCommitRowResult> findFinished(UUID commitId, String rowKey) {
        List<ExcelCommitRowResult> found = jdbcTemplate.query(
                "SELECT status, result_action, tangible_asset_id, error_message " +
                        "FROM excel_commit_row WHERE commit_id = ? AND row_key = ? " +
                        "AND status IN ('COMPLETED', 'FAILED')",
                ROW_MAPPER, commitId, rowKey);
        return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
    }

    @Override
    public boolean claimRow(UUID commitId, String rowKey, int rowNum) {
        int rows = jdbcTemplate.update(
                "INSERT INTO excel_commit_row (commit_id, row_key, row_num, status, create_at, update_at) " +
                        "VALUES (?, ?, ?, 'IN_PROGRESS', now(), now()) " +
                        "ON CONFLICT (commit_id, row_key) DO UPDATE " +
                        "    SET update_at = now() " +
                        "    WHERE excel_commit_row.status = 'IN_PROGRESS'",
                commitId, rowKey, rowNum);
        return rows == 1;
    }

    @Override
    public void completeRow(UUID commitId, String rowKey, ExcelCommitRowResult result) {
        jdbcTemplate.update(
                "UPDATE excel_commit_row " +
                        "SET status = 'COMPLETED', result_action = ?, tangible_asset_id = ?, error_message = NULL, update_at = now() " +
                        "WHERE commit_id = ? AND row_key = ?",
                result.getResultAction(), result.getTangibleAssetId(), commitId, rowKey);
    }

    @Override
    public void failRow(UUID commitId, String rowKey, int rowNum, String errorMessage) {
        // claimRow의 INSERT가 이 행의 실패로 함께 롤백됐을 수 있으므로(같은 REQUIRES_NEW 트랜잭션
        // 안에서 실패했다면), 이 메서드는 항상 별도의 새 트랜잭션에서 호출되어야 하고 UPSERT로
        // 안전하게 최종 FAILED 상태를 남긴다.
        jdbcTemplate.update(
                "INSERT INTO excel_commit_row (commit_id, row_key, row_num, status, error_message, create_at, update_at) " +
                        "VALUES (?, ?, ?, 'FAILED', ?, now(), now()) " +
                        "ON CONFLICT (commit_id, row_key) DO UPDATE " +
                        "    SET status = 'FAILED', error_message = EXCLUDED.error_message, update_at = now() " +
                        "    WHERE excel_commit_row.status <> 'COMPLETED'",
                commitId, rowKey, rowNum, errorMessage);
    }
}
