package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitRowResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * S-213 엑셀 확정 행(row) 단위 멱등성(ExcelCommitRowStoreImpl) 검증. commit_id+row_key 기본키와
 * claimRow의 조건부 UPSERT(WHERE status = 'IN_PROGRESS')가 실제 Postgres에서 의도대로 동작하는지
 * 확인해야 하므로 실제 DB에 붙는 통합 테스트로 작성한다.
 */
@SpringBootTest
class ExcelCommitRowStoreImplTest {

    @Autowired
    private ExcelCommitRowStoreImpl store;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID commitId;

    @BeforeEach
    void setUp() {
        commitId = UUID.randomUUID();
        // excel_commit_row.commit_id는 excel_commit_request를 참조하는 FK이므로 부모 행이 먼저 필요하다
        jdbcTemplate.update(
                "INSERT INTO excel_commit_request (commit_id, status, request_hash, attempt_count, create_at, update_at) " +
                        "VALUES (?, 'IN_PROGRESS', 'test-hash', 1, now(), now())",
                commitId);
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM excel_commit_row WHERE commit_id = ?", commitId);
        jdbcTemplate.update("DELETE FROM excel_commit_request WHERE commit_id = ?", commitId);
    }

    @Test
    void 처음_보는_행은_claim에_성공한다() {
        assertThat(store.claimRow(commitId, "1-abcd", 1)).isTrue();
    }

    @Test
    void 이미_COMPLETED된_행은_다시_claim할_수_없다() {
        store.claimRow(commitId, "1-abcd", 1);
        store.completeRow(commitId, "1-abcd", ExcelCommitRowResult.success("CREATED", UUID.randomUUID()));

        assertThat(store.claimRow(commitId, "1-abcd", 1)).isFalse();
    }

    @Test
    void 이미_FAILED된_행은_다시_claim할_수_없다() {
        store.claimRow(commitId, "1-abcd", 1);
        store.failRow(commitId, "1-abcd", 1, "오류 발생");

        assertThat(store.claimRow(commitId, "1-abcd", 1)).isFalse();
    }

    @Test
    void 이전_시도가_죽어_IN_PROGRESS로_남은_행은_재claim할_수_있다() {
        store.claimRow(commitId, "1-abcd", 1); // 완료/실패 기록 없이 그대로 방치(죽은 시도 흉내)

        assertThat(store.claimRow(commitId, "1-abcd", 1)).isTrue();
    }

    @Test
    void completeRow_이후_findFinished가_결과를_반환한다() {
        UUID assetId = UUID.randomUUID();
        store.claimRow(commitId, "1-abcd", 1);
        store.completeRow(commitId, "1-abcd", ExcelCommitRowResult.success("CREATED", assetId));

        Optional<ExcelCommitRowResult> found = store.findFinished(commitId, "1-abcd");

        assertThat(found).isPresent();
        assertThat(found.get().isSuccess()).isTrue();
        assertThat(found.get().getTangibleAssetId()).isEqualTo(assetId);
    }

    @Test
    void failRow_이후_findFinished가_실패_결과를_반환한다() {
        store.claimRow(commitId, "1-abcd", 1);
        store.failRow(commitId, "1-abcd", 1, "카테고리를 찾을 수 없습니다");

        Optional<ExcelCommitRowResult> found = store.findFinished(commitId, "1-abcd");

        assertThat(found).isPresent();
        assertThat(found.get().isSuccess()).isFalse();
        assertThat(found.get().getErrorMessage()).isEqualTo("카테고리를 찾을 수 없습니다");
    }

    @Test
    void 아직_처리중인_행은_findFinished에_나타나지_않는다() {
        store.claimRow(commitId, "1-abcd", 1);

        assertThat(store.findFinished(commitId, "1-abcd")).isEmpty();
    }

    @Test
    void failRow는_claim_없이도_UPSERT로_기록된다() {
        // claimRow의 INSERT가 같은 트랜잭션 안에서 롤백된 상황(자산 저장 실패)을 흉내낸다 -
        // failRow는 claim 여부와 무관하게 항상 최종 FAILED 상태를 남길 수 있어야 한다.
        store.failRow(commitId, "1-abcd", 1, "자산 저장 실패");

        Optional<ExcelCommitRowResult> found = store.findFinished(commitId, "1-abcd");
        assertThat(found).isPresent();
        assertThat(found.get().isSuccess()).isFalse();
    }
}
