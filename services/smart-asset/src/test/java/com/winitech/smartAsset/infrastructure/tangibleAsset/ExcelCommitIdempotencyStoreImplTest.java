package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitClaim;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelCommitResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * S-213 엑셀 확정 요청 레벨 멱등성(ExcelCommitIdempotencyStoreImpl) - claim의 INSERT ... ON CONFLICT,
 * lease 만료 후 재claim, FAILED 이후 재claim, request_hash 불일치 거부 등 실제 Postgres 동작에
 * 의존하는 부분을 실제 DB에 붙는 통합 테스트로 검증한다.
 */
@SpringBootTest
class ExcelCommitIdempotencyStoreImplTest {

    @Autowired
    private ExcelCommitIdempotencyStoreImpl store;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM excel_commit_row WHERE commit_id IN " +
                "(SELECT commit_id FROM excel_commit_request WHERE requested_by IS NULL)");
        jdbcTemplate.update("DELETE FROM excel_commit_request WHERE requested_by IS NULL");
    }

    private TangibleAssetExcelCommitResult sampleResult() {
        return TangibleAssetExcelCommitResult.builder()
                .createdCount(1).updatedCount(0).skippedCount(0).failedRows(new ArrayList<>()).build();
    }

    @Test
    void 처음_보는_commitId는_claim에_성공한다() {
        UUID commitId = UUID.randomUUID();

        ExcelCommitClaim claim = store.claim(commitId, null, "hash-a");

        assertThat(claim.getOutcome()).isEqualTo(ExcelCommitClaim.Outcome.CLAIMED);
    }

    @Test
    void 같은_내용으로_COMPLETED된_요청은_결과를_그대로_반환한다() {
        UUID commitId = UUID.randomUUID();
        store.claim(commitId, null, "hash-a");
        store.complete(commitId, sampleResult());

        ExcelCommitClaim claim = store.claim(commitId, null, "hash-a");

        assertThat(claim.getOutcome()).isEqualTo(ExcelCommitClaim.Outcome.ALREADY_COMPLETED);
        assertThat(claim.getCompletedResult().getCreatedCount()).isEqualTo(1);
    }

    @Test
    void 같은_commitId에_다른_내용이면_거부된다() {
        UUID commitId = UUID.randomUUID();
        store.claim(commitId, null, "hash-a");

        ExcelCommitClaim claim = store.claim(commitId, null, "hash-b");

        assertThat(claim.getOutcome()).isEqualTo(ExcelCommitClaim.Outcome.REQUEST_MISMATCH);
    }

    @Test
    void 유효한_lease를_가진_IN_PROGRESS_요청은_중복_claim을_거부한다() {
        UUID commitId = UUID.randomUUID();
        store.claim(commitId, null, "hash-a"); // lease 아직 유효(now() + 10분)

        ExcelCommitClaim claim = store.claim(commitId, null, "hash-a");

        assertThat(claim.getOutcome()).isEqualTo(ExcelCommitClaim.Outcome.IN_PROGRESS_ELSEWHERE);
    }

    @Test
    void lease가_만료된_IN_PROGRESS_요청은_재claim에_성공한다() {
        UUID commitId = UUID.randomUUID();
        store.claim(commitId, null, "hash-a");
        // 처리 프로세스가 죽어 lease가 지난 상황을 흉내낸다
        jdbcTemplate.update(
                "UPDATE excel_commit_request SET lease_expires_at = now() - interval '1 minute' WHERE commit_id = ?",
                commitId);

        ExcelCommitClaim claim = store.claim(commitId, null, "hash-a");

        assertThat(claim.getOutcome()).isEqualTo(ExcelCommitClaim.Outcome.CLAIMED);
        Integer attemptCount = jdbcTemplate.queryForObject(
                "SELECT attempt_count FROM excel_commit_request WHERE commit_id = ?", Integer.class, commitId);
        assertThat(attemptCount).isEqualTo(2);
    }

    @Test
    void FAILED_요청은_재claim에_성공한다() {
        UUID commitId = UUID.randomUUID();
        store.claim(commitId, null, "hash-a");
        store.fail(commitId, "예상 못한 오류");

        ExcelCommitClaim claim = store.claim(commitId, null, "hash-a");

        assertThat(claim.getOutcome()).isEqualTo(ExcelCommitClaim.Outcome.CLAIMED);
    }

    @Test
    void complete_이후에는_last_error가_비워진다() {
        UUID commitId = UUID.randomUUID();
        store.claim(commitId, null, "hash-a");
        store.fail(commitId, "일시적 오류");
        store.claim(commitId, null, "hash-a"); // 재claim
        store.complete(commitId, sampleResult());

        String lastError = jdbcTemplate.queryForObject(
                "SELECT last_error FROM excel_commit_request WHERE commit_id = ?", String.class, commitId);
        assertThat(lastError).isNull();
    }
}
