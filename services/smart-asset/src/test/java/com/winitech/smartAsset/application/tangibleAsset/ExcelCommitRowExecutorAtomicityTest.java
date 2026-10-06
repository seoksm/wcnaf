package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.tangibleAsset.ExcelCommitRowStore;
import com.winitech.smartAsset.domain.tangibleAsset.ExcelRowSigner;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelRow;
import com.winitech.smartAsset.infrastructure.assetCategory.AssetCategoryRepository;
import com.winitech.smartAsset.infrastructure.assetLocation.AssetLocationRepository;
import com.winitech.smartAsset.infrastructure.tangibleAsset.TangibleAssetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * S-213 엑셀 확정 요구사항 C-5: "CREATE 자산 저장과 행 완료 기록 사이에 프로세스가 종료돼도 둘 다
 * 커밋되거나 둘 다 롤백되어야 한다"를 실제 DB 트랜잭션으로 검증한다.
 * <p>
 * ExcelCommitRowStore를 @MockBean으로 교체해 completeRow()가 실패하는 상황을 인위적으로 만들고,
 * 그 실패가 (같은 REQUIRES_NEW 트랜잭션 안에서 실행된) 실제 자산 저장까지 함께 롤백시키는지 실제
 * DB 조회로 확인한다. claimRow는 두 시나리오 모두 true로 고정해, "claim은 됐지만 그 다음이 실패하는"
 * 상황에 집중한다.
 */
@SpringBootTest
class ExcelCommitRowExecutorAtomicityTest {

    @Autowired
    private ExcelCommitRowExecutor executor;

    @Autowired
    private ExcelRowSigner excelRowSigner;

    @Autowired
    private TangibleAssetRepository tangibleAssetRepository;

    @Autowired
    private AssetCategoryRepository assetCategoryRepository;

    @Autowired
    private AssetLocationRepository assetLocationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private ExcelCommitRowStore excelCommitRowStore;

    private AssetCategory testCategory;
    private AssetLocation testLocation;
    private static final String DISTINCTIVE_ASSET_NAME = "원자성테스트자산-atomicity-marker";

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        when(excelCommitRowStore.claimRow(any(), any(), anyInt())).thenReturn(true);
        testCategory = assetCategoryRepository.save(AssetCategory.builder()
                .categoryCode("ATOMIC-TEST-CAT").categoryName("원자성테스트카테고리").usefulLifeMonths(36).build());
        testLocation = assetLocationRepository.save(AssetLocation.builder().locationName("원자성테스트위치").build());
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM asset_history WHERE tangible_asset_id IN " +
                "(SELECT tangible_asset_id FROM tangible_asset WHERE asset_name = ?)", DISTINCTIVE_ASSET_NAME);
        jdbcTemplate.update("DELETE FROM tangible_asset WHERE asset_name = ?", DISTINCTIVE_ASSET_NAME);
        jdbcTemplate.update("DELETE FROM asset_category WHERE asset_category_id = ?", testCategory.getId());
        jdbcTemplate.update("DELETE FROM asset_location WHERE asset_location_id = ?", testLocation.getId());
        RequestContextHolder.resetRequestAttributes();
    }

    private TangibleAssetExcelRow signedCreateRow(UUID categoryId, UUID locationId) {
        TangibleAssetExcelRow row = TangibleAssetExcelRow.builder()
                .rowNum(1)
                .action("CREATE")
                .assetName(DISTINCTIVE_ASSET_NAME)
                .categoryId(categoryId)
                .locationId(locationId)
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

    private long countDistinctiveAsset() {
        return tangibleAssetRepository.findAllByContainsKeyword(null, org.springframework.data.domain.PageRequest.of(0, 500))
                .getContent().stream().filter(a -> DISTINCTIVE_ASSET_NAME.equals(a.getAssetName())).count();
    }

    @Test
    void 자산_저장이_실패하면_행_완료_기록도_남지_않는다() {
        // 존재하지 않는 카테고리ID로 자산 생성 자체가 실패하는 상황
        TangibleAssetExcelRow row = signedCreateRow(UUID.randomUUID(), testLocation.getId());

        assertThatThrownBy(() -> executor.processInNewTransaction(UUID.randomUUID(), "1-x", row, UUID.randomUUID()));

        assertThat(countDistinctiveAsset()).isZero();
    }

    @Test
    void 행_완료_기록이_실패하면_자산_저장도_롤백된다() {
        doThrow(new RuntimeException("시뮬레이션된 DB 오류")).when(excelCommitRowStore).completeRow(any(), any(), any());

        TangibleAssetExcelRow row = signedCreateRow(testCategory.getId(), testLocation.getId());

        assertThatThrownBy(() -> executor.processInNewTransaction(UUID.randomUUID(), "1-x", row, UUID.randomUUID()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("시뮬레이션된 DB 오류");

        // completeRow가 실패했으므로, 같은 트랜잭션 안에서 실제로 실행됐던 자산 INSERT도 롤백되어
        // DB에는 이 자산이 전혀 남아있지 않아야 한다.
        assertThat(countDistinctiveAsset()).isZero();
    }
}
