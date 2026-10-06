package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.tangibleAsset.*;
import com.winitech.smartAsset.infrastructure.assetCategory.AssetCategoryRepository;
import com.winitech.smartAsset.infrastructure.assetLocation.AssetLocationRepository;
import com.winitech.smartAsset.infrastructure.tangibleAsset.TangibleAssetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * S-213 엑셀 확정의 요청 레벨 멱등성/장애 복구 시나리오를 실제 DB·실제 빈으로 검증하는 통합 테스트.
 * TangibleAssetExcelFacade, ExcelCommitIdempotencyStore, ExcelCommitRowProcessor 모두 실제 구현을
 * 쓴다 - 이 기능들의 가치는 정확히 "여러 컴포넌트가 실제 DB 트랜잭션 경계를 사이에 두고 맞물려
 * 동작하는가"에 있으므로, 여기서만큼은 모킹으로 대체하지 않는다.
 */
@SpringBootTest
class TangibleAssetExcelCommitIdempotencyIntegrationTest {

    @Autowired private TangibleAssetExcelFacade facade;
    @Autowired private ExcelRowSigner excelRowSigner;
    @Autowired private ExcelCommitIdempotencyStore idempotencyStore;
    @Autowired private ExcelCommitRowProcessor excelCommitRowProcessor;
    @Autowired private TangibleAssetRepository tangibleAssetRepository;
    @Autowired private AssetCategoryRepository assetCategoryRepository;
    @Autowired private AssetLocationRepository assetLocationRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private AssetCategory testCategory;
    private AssetLocation testLocation;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        testCategory = assetCategoryRepository.save(AssetCategory.builder()
                .categoryCode("IDEMP-IT-CAT").categoryName("멱등성통합테스트카테고리").usefulLifeMonths(36).build());
        testLocation = assetLocationRepository.save(AssetLocation.builder().locationName("멱등성통합테스트위치").build());
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM asset_history WHERE tangible_asset_id IN " +
                "(SELECT tangible_asset_id FROM tangible_asset WHERE asset_name LIKE '멱등성테스트%')");
        jdbcTemplate.update("DELETE FROM tangible_asset WHERE asset_name LIKE '멱등성테스트%'");
        jdbcTemplate.update("DELETE FROM excel_commit_row WHERE commit_id IN " +
                "(SELECT commit_id FROM excel_commit_request WHERE requested_by IS NULL)");
        jdbcTemplate.update("DELETE FROM excel_commit_request WHERE requested_by IS NULL");
        jdbcTemplate.update("DELETE FROM asset_category WHERE asset_category_id = ?", testCategory.getId());
        jdbcTemplate.update("DELETE FROM asset_location WHERE asset_location_id = ?", testLocation.getId());
        RequestContextHolder.resetRequestAttributes();
    }

    private TangibleAssetExcelRow signedCreateRow(int rowNum, String assetName) {
        TangibleAssetExcelRow row = TangibleAssetExcelRow.builder()
                .rowNum(rowNum)
                .action("CREATE")
                .assetName(assetName)
                .categoryId(testCategory.getId())
                .locationId(testLocation.getId())
                .lifeStatus("USE")
                .assignType("UNASSIGNED")
                .acquisitionDate("2026-01-01")
                .acquisitionAmount("1000000")
                .modelName("")
                .manufacturer("")
                .serialNo("")
                .memo("")
                .build();
        row.setSignature(excelRowSigner.sign(row));
        return row;
    }

    private long countByAssetName(String assetName) {
        return tangibleAssetRepository.findAllByContainsKeyword(null, org.springframework.data.domain.PageRequest.of(0, 500))
                .getContent().stream().filter(a -> assetName.equals(a.getAssetName())).count();
    }

    @Test
    void 동일_commitId로_동일_요청을_재전송하면_이전_결과를_그대로_반환하고_중복_생성하지_않는다() {
        UUID commitId = UUID.randomUUID();
        List<TangibleAssetExcelRow> rows = List.of(signedCreateRow(1, "멱등성테스트-재전송"));

        TangibleAssetExcelCommitResult first = facade.commit(commitId, rows);
        TangibleAssetExcelCommitResult second = facade.commit(commitId, rows);

        assertThat(first.getCreatedCount()).isEqualTo(1);
        assertThat(second.getCreatedCount()).isEqualTo(1);
        assertThat(countByAssetName("멱등성테스트-재전송")).isEqualTo(1);
    }

    @Test
    void 동일_commitId에_다른_내용의_요청이면_충돌로_거부한다() {
        UUID commitId = UUID.randomUUID();
        facade.commit(commitId, List.of(signedCreateRow(1, "멱등성테스트-원본")));

        List<TangibleAssetExcelRow> differentRows = List.of(signedCreateRow(1, "멱등성테스트-변조본"));

        assertThatThrownBy(() -> facade.commit(commitId, differentRows))
                .isInstanceOf(InvalidParamException.class)
                .hasMessageContaining("다른 내용");
        assertThat(countByAssetName("멱등성테스트-변조본")).isZero();
    }

    @Test
    void 유효한_lease를_가진_처리중_요청은_거부하고_lease_만료_후에는_재시도에_성공한다() {
        UUID commitId = UUID.randomUUID();
        List<TangibleAssetExcelRow> rows = List.of(signedCreateRow(1, "멱등성테스트-lease"));
        String requestHash = facade.computeRequestHash(rows);

        // 다른 인스턴스가 이미 유효한 lease로 처리 중인 상황을 직접 만든다.
        ExcelCommitClaim claimed = idempotencyStore.claim(commitId, null, requestHash);
        assertThat(claimed.getOutcome()).isEqualTo(ExcelCommitClaim.Outcome.CLAIMED);

        assertThatThrownBy(() -> facade.commit(commitId, rows))
                .isInstanceOf(InvalidParamException.class)
                .hasMessageContaining("처리 중");
        assertThat(countByAssetName("멱등성테스트-lease")).isZero();

        // 그 시도가 죽어 lease가 지난 상황으로 전환한다.
        jdbcTemplate.update(
                "UPDATE excel_commit_request SET lease_expires_at = now() - interval '1 minute' WHERE commit_id = ?",
                commitId);

        TangibleAssetExcelCommitResult result = facade.commit(commitId, rows);

        assertThat(result.getCreatedCount()).isEqualTo(1);
        assertThat(countByAssetName("멱등성테스트-lease")).isEqualTo(1);
    }

    @Test
    void 첫_행_완료_후_프로세스_장애를_모의하고_재시도해도_CREATE가_중복되지_않는다() {
        UUID commitId = UUID.randomUUID();
        TangibleAssetExcelRow row1 = signedCreateRow(1, "멱등성테스트-행1");
        TangibleAssetExcelRow row2 = signedCreateRow(2, "멱등성테스트-행2");
        List<TangibleAssetExcelRow> rows = List.of(row1, row2);
        String requestHash = facade.computeRequestHash(rows);

        // 1차 시도: row1까지만 실제로 처리(REQUIRES_NEW 트랜잭션으로 커밋됨)하고, row2는 처리하지
        // 않은 채 프로세스가 죽어 요청 레벨은 FAILED로 남은 상황을 재현한다.
        idempotencyStore.claim(commitId, null, requestHash);
        UUID batchId = UUID.randomUUID();
        ExcelCommitRowResult row1Result = excelCommitRowProcessor.processRow(commitId, row1, batchId);
        assertThat(row1Result.isSuccess()).isTrue();
        idempotencyStore.fail(commitId, "시뮬레이션: 프로세스가 row2 처리 전에 종료됨");

        // 2차 시도(재시도): 같은 commitId, 같은 rows로 처음부터 다시 확정을 요청한다.
        TangibleAssetExcelCommitResult result = facade.commit(commitId, rows);

        assertThat(result.getCreatedCount()).isEqualTo(2); // row1(재사용) + row2(신규) = 2건 반영
        assertThat(countByAssetName("멱등성테스트-행1")).isEqualTo(1); // row1은 재실행되지 않아 중복 생성 없음
        assertThat(countByAssetName("멱등성테스트-행2")).isEqualTo(1);
    }

    @Test
    void 여러_인스턴스가_동시에_같은_commitId를_처리해도_자산이_중복_생성되지_않는다() throws Exception {
        UUID commitId = UUID.randomUUID();
        List<TangibleAssetExcelRow> rows = List.of(signedCreateRow(1, "멱등성테스트-동시성"));

        CyclicBarrier barrier = new CyclicBarrier(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Callable<String> task = () -> {
                RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
                try {
                    barrier.await(5, TimeUnit.SECONDS);
                    TangibleAssetExcelCommitResult result = facade.commit(commitId, rows);
                    return "OK:" + result.getCreatedCount();
                } catch (Exception e) {
                    return "REJECTED:" + e.getClass().getSimpleName();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            };

            List<Future<String>> futures = executor.invokeAll(List.of(task, task));
            List<String> results = futures.stream().map(f -> {
                try {
                    return f.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

            // 둘 중 하나는 실제로 처리하고, 다른 하나는 "처리 중" 거부를 받거나 뒤이어 완료된 결과를
            // 그대로 재사용한다 - 어느 쪽이든 자산은 정확히 1건만 생성돼야 한다(중복 없음).
            assertThat(results).isNotEmpty();
            assertThat(countByAssetName("멱등성테스트-동시성")).isEqualTo(1);
        } finally {
            executor.shutdown();
        }
    }
}
