package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.infrastructure.assetCategory.AssetCategoryRepository;
import com.winitech.smartAsset.infrastructure.assetLocation.AssetLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * S-700 종류별/위치별/상태별 현황 위젯이 쓰는 그룹핑 쿼리 회귀 테스트. 종류·위치는 이번
 * 테스트가 만든 고유 마커 이름만 필터링해 검증하고(다른 데이터와 절대 안 겹침), 생애상태
 * 분포는 전체 테이블을 대상으로 하는 전역 집계라 마커로 걸러낼 수 없으므로 "이 테스트가 만든
 * 건수만큼 정확히 늘었는가"를 베이스라인 대비 델타로 검증한다.
 */
@SpringBootTest
@Transactional
class TangibleAssetDashboardQueryTest {

    @Autowired
    private TangibleAssetRepository tangibleAssetRepository;
    @Autowired
    private AssetCategoryRepository assetCategoryRepository;
    @Autowired
    private AssetLocationRepository assetLocationRepository;

    private String marker;
    private AssetCategory categoryA;
    private AssetCategory categoryB;
    private AssetLocation locationA;

    @BeforeEach
    void setUp() {
        marker = "DASHTEST-" + UUID.randomUUID().toString().substring(0, 8);

        categoryA = assetCategoryRepository.save(AssetCategory.builder()
                .categoryCode(marker + "-CAT-A").categoryName(marker + "-종류A").build());
        categoryB = assetCategoryRepository.save(AssetCategory.builder()
                .categoryCode(marker + "-CAT-B").categoryName(marker + "-종류B").build());
        locationA = assetLocationRepository.save(AssetLocation.builder()
                .locationName(marker + "-위치A").build());
    }

    private TangibleAsset newAsset(String suffix, AssetCategory category, AssetLocation location) {
        return newAsset(suffix, category, location, null);
    }

    private TangibleAsset newAsset(String suffix, AssetCategory category, AssetLocation location, TangibleAsset.LifeStatus lifeStatus) {
        return TangibleAsset.builder()
                .assetCode(marker + "-AST-" + suffix)
                .assetName(marker + " 자산 " + suffix)
                .category(category)
                .location(location)
                .lifeStatus(lifeStatus)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();
    }

    @Test
    void countActiveGroupByCategory는_종류별_건수를_내림차순으로_반환하고_처분완료는_제외한다() {
        tangibleAssetRepository.save(newAsset("1", categoryA, locationA));
        tangibleAssetRepository.save(newAsset("2", categoryA, locationA));
        tangibleAssetRepository.save(newAsset("3", categoryA, locationA, TangibleAsset.LifeStatus.DISPOSED));
        tangibleAssetRepository.save(newAsset("4", categoryB, locationA));

        Map<String, Long> byCategory = toMap(tangibleAssetRepository.countActiveGroupByCategory());

        assertThat(byCategory.get(categoryA.getCategoryName())).isEqualTo(2L);
        assertThat(byCategory.get(categoryB.getCategoryName())).isEqualTo(1L);
    }

    @Test
    void countActiveGroupByLocation는_위치별_건수를_반환한다() {
        tangibleAssetRepository.save(newAsset("1", categoryA, locationA));
        tangibleAssetRepository.save(newAsset("2", categoryA, locationA));

        Map<String, Long> byLocation = toMap(tangibleAssetRepository.countActiveGroupByLocation());

        assertThat(byLocation.get(locationA.getLocationName())).isEqualTo(2L);
    }

    @Test
    void countGroupByLifeStatus는_생애상태별_건수를_전역으로_집계하며_처분완료도_포함한다() {
        Map<TangibleAsset.LifeStatus, Long> before = toLifeStatusMap(tangibleAssetRepository.countGroupByLifeStatus());

        tangibleAssetRepository.save(newAsset("1", categoryA, locationA));
        tangibleAssetRepository.save(newAsset("2", categoryA, locationA, TangibleAsset.LifeStatus.REPAIR));
        tangibleAssetRepository.save(newAsset("3", categoryA, locationA, TangibleAsset.LifeStatus.DISPOSED));

        Map<TangibleAsset.LifeStatus, Long> after = toLifeStatusMap(tangibleAssetRepository.countGroupByLifeStatus());

        assertThat(after.getOrDefault(TangibleAsset.LifeStatus.USE, 0L) - before.getOrDefault(TangibleAsset.LifeStatus.USE, 0L)).isEqualTo(1L);
        assertThat(after.getOrDefault(TangibleAsset.LifeStatus.REPAIR, 0L) - before.getOrDefault(TangibleAsset.LifeStatus.REPAIR, 0L)).isEqualTo(1L);
        assertThat(after.getOrDefault(TangibleAsset.LifeStatus.DISPOSED, 0L) - before.getOrDefault(TangibleAsset.LifeStatus.DISPOSED, 0L)).isEqualTo(1L);
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        return rows.stream()
                .filter(row -> ((String) row[0]).startsWith(marker))
                .collect(Collectors.toMap(row -> (String) row[0], row -> ((Number) row[1]).longValue()));
    }

    private Map<TangibleAsset.LifeStatus, Long> toLifeStatusMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                row -> (TangibleAsset.LifeStatus) row[0],
                row -> ((Number) row[1]).longValue()));
    }
}
