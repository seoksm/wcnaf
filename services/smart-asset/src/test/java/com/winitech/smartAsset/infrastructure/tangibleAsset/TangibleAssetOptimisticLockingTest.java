package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetCommand;
import com.winitech.smartAsset.infrastructure.assetCategory.AssetCategoryRepository;
import com.winitech.smartAsset.infrastructure.assetLocation.AssetLocationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
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

/**
 * Q-16/D8: TangibleAsset.version(@Version 낙관적 잠금) - 같은 자산을 동시에 수정하는 두 트랜잭션
 * 중 하나만 성공해야 한다는 요구사항을 결정론적으로 검증한다.
 * <p>
 * 단순히 두 스레드를 동시에 실행시키는 것만으로는 "실제로 같은 버전을 동시에 읽었는지"가 타이밍에
 * 좌우돼 테스트가 가끔 통과/실패하는 flaky 테스트가 될 수 있다. 이를 피하기 위해 두 트랜잭션 모두
 * findById로 같은 버전을 읽어들인 "직후", 실제 수정을 반영하기 "직전" 지점에 CyclicBarrier를 두어
 * 두 트랜잭션이 반드시 같은 버전을 먼저 읽은 뒤에만 각자 수정을 진행하도록 강제한다 - 이러면 둘 중
 * 하나는 반드시 충돌해야 한다(타이밍에 좌우되지 않는다).
 */
@SpringBootTest
class TangibleAssetOptimisticLockingTest {

    @Autowired
    private TangibleAssetRepository tangibleAssetRepository;

    @Autowired
    private AssetCategoryRepository assetCategoryRepository;

    @Autowired
    private AssetLocationRepository assetLocationRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private AssetCategory testCategory;
    private AssetLocation testLocation;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        testCategory = assetCategoryRepository.save(AssetCategory.builder()
                .categoryCode("LOCK-TEST-CAT").categoryName("잠금테스트카테고리").usefulLifeMonths(36).build());
        testLocation = assetLocationRepository.save(AssetLocation.builder().locationName("잠금테스트위치").build());
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM tangible_asset WHERE asset_code LIKE 'AST-920%'");
        jdbcTemplate.update("DELETE FROM asset_category WHERE asset_category_id = ?", testCategory.getId());
        jdbcTemplate.update("DELETE FROM asset_location WHERE asset_location_id = ?", testLocation.getId());
    }

    private Callable<String> updateNameTask(UUID assetId, String newName, CyclicBarrier barrier) {
        return () -> {
            try {
                transactionTemplate.execute(status -> {
                    TangibleAsset asset = tangibleAssetRepository.findById(assetId).orElseThrow();

                    TangibleAssetCommand.UpdateCommand command = TangibleAssetCommand.UpdateCommand.builder()
                            .tangibleAssetId(assetId)
                            .assetName(newName)
                            .lifeStatus(asset.getLifeStatus())
                            .assignType(TangibleAsset.AssignType.UNASSIGNED)
                            .acquisitionDate(asset.getAcquisitionDate())
                            .acquisitionAmount(asset.getAcquisitionAmount())
                            .build();

                    awaitBarrier(barrier);

                    asset.modify(command, testCategory, testLocation);
                    tangibleAssetRepository.saveAndFlush(asset);
                    return null;
                });
                return "OK";
            } catch (Exception e) {
                Throwable root = e;
                while (root.getCause() != null) {
                    root = root.getCause();
                }
                return "FAIL:" + root.getClass().getSimpleName();
            }
        };
    }

    private void awaitBarrier(CyclicBarrier barrier) {
        try {
            barrier.await(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void 같은_자산을_두_트랜잭션이_동시에_수정하면_하나만_성공한다() throws Exception {
        TangibleAsset saved = tangibleAssetRepository.save(TangibleAsset.builder()
                .assetCode("AST-9201-LOCK")
                .assetName("원본이름")
                .category(testCategory)
                .location(testLocation)
                .acquisitionDate(LocalDate.of(2024, 1, 1))
                .acquisitionAmount(BigDecimal.valueOf(1_000_000))
                .build());
        UUID assetId = saved.getId();
        assertThat(saved.getVersion()).isEqualTo(0L);

        CyclicBarrier barrier = new CyclicBarrier(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            List<Future<String>> futures = executor.invokeAll(List.of(
                    updateNameTask(assetId, "이름A", barrier),
                    updateNameTask(assetId, "이름B", barrier)
            ));

            List<String> results = futures.stream().map(f -> {
                try {
                    return f.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

            assertThat(results).filteredOn("OK"::equals).hasSize(1);
            assertThat(results).filteredOn(r -> r.startsWith("FAIL:")).hasSize(1);
            assertThat(results).anyMatch(r -> r.contains("OptimisticLock") || r.contains("StaleObjectState") || r.contains("StaleState"));

            TangibleAsset reloaded = tangibleAssetRepository.findById(assetId).orElseThrow();
            assertThat(reloaded.getVersion()).isEqualTo(1L);
            assertThat(reloaded.getAssetName()).isIn("이름A", "이름B");
        } finally {
            executor.shutdown();
        }
    }
}
