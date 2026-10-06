package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.infrastructure.assetCategory.AssetCategoryRepository;
import com.winitech.smartAsset.infrastructure.assetLocation.AssetLocationRepository;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 유형자산 목록 페이지네이션(page/size/sort) + N+1 제거(@EntityGraph) 검증.
 * 실제 Postgres에 붙는 통합 테스트로, 서로 다른 종류(category)·위치(location)를 가진 자산을
 * 여러 건 만들어 category/location 지연 로딩이 실제로 한 번의 조회로 끝나는지 Hibernate
 * Statistics로 확인한다. 다른 테스트/운영 데이터와 섞이지 않도록 고유한 마커를 자산명에 넣는다.
 */
@SpringBootTest
@Transactional
class TangibleAssetRepositoryTest {

    @Autowired
    private TangibleAssetRepository tangibleAssetRepository;
    @Autowired
    private AssetCategoryRepository assetCategoryRepository;
    @Autowired
    private AssetLocationRepository assetLocationRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private String marker;
    private final List<AssetCategory> categories = new ArrayList<>();
    private final List<AssetLocation> locations = new ArrayList<>();

    @BeforeEach
    void setUp() {
        marker = "PAGETEST-" + UUID.randomUUID().toString().substring(0, 8);

        // 서로 다른 category/location을 5개씩 만들어, entity graph 없이는 지연 로딩 시
        // 최소 5+5번의 추가 쿼리가 나갈 수밖에 없는 상황을 구성한다.
        for (int i = 0; i < 5; i++) {
            AssetCategory category = assetCategoryRepository.save(AssetCategory.builder()
                    .categoryCode(marker + "-CAT-" + i)
                    .categoryName(marker + " 종류 " + i)
                    .build());
            categories.add(category);

            AssetLocation location = assetLocationRepository.save(AssetLocation.builder()
                    .locationName(marker + " 위치 " + i)
                    .build());
            locations.add(location);

            tangibleAssetRepository.save(TangibleAsset.builder()
                    .assetCode(marker + "-AST-" + i)
                    .assetName(marker + " 자산 " + i)
                    .category(category)
                    .location(location)
                    .acquisitionDate(LocalDate.of(2026, 1, 1 + i))
                    .acquisitionAmount(new BigDecimal("1000000"))
                    .build());
        }
    }

    @AfterEach
    void tearDown() {
        // @Transactional 테스트는 기본적으로 각 테스트 후 롤백되므로 별도 정리가 필요 없다.
    }

    private Statistics statistics() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        return statistics;
    }

    @Test
    void EntityGraph_덕분에_category_location_지연로딩이_N1을_만들지_않는다() {
        Statistics statistics = statistics();

        Page<TangibleAsset> page = tangibleAssetRepository.findAllByContainsKeyword(
                marker, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "assetName")));

        // TangibleAssetInfo 생성자가 실제로 하는 것과 같은 접근을 재현한다.
        for (TangibleAsset asset : page.getContent()) {
            assertThat(asset.getCategory().getCategoryName()).startsWith(marker);
            assertThat(asset.getLocation().getLocationName()).startsWith(marker);
        }

        // entity graph가 없었다면 서로 다른 category 5개 + location 5개에 대해 지연 로딩이 각각
        // 추가 쿼리를 발생시켜 최소 1(count) + 1(본문) + 5 + 5 = 12건 이상이 됐을 것이다.
        // 지금은 본문 조회 + count 쿼리 및 트랜잭션 부수 작업 정도로 몇 건 안에서 끝나야 하며,
        // 무엇보다 "서로 다른 category/location 개수(5개)"에 비례해서 늘어나서는 안 된다.
        assertThat(statistics.getPrepareStatementCount())
                .as("category/location을 함께 즉시 로딩해 지연 로딩으로 인한 추가 쿼리(5개 종류/위치에 비례)가 없어야 한다")
                .isLessThanOrEqualTo(5);
    }

    @Test
    void 키워드로_검색하면_해당_자산만_반환된다() {
        Page<TangibleAsset> page = tangibleAssetRepository.findAllByContainsKeyword(
                marker + "-AST-2", PageRequest.of(0, 10, Sort.by("assetName")));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getAssetCode()).isEqualTo(marker + "-AST-2");
    }

    @Test
    void assetName_오름차순_정렬이_적용된다() {
        Page<TangibleAsset> page = tangibleAssetRepository.findAllByContainsKeyword(
                marker, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "assetName")));

        List<String> names = page.getContent().stream().map(TangibleAsset::getAssetName).toList();
        List<String> sorted = new ArrayList<>(names);
        sorted.sort(String::compareTo);
        assertThat(names).isEqualTo(sorted);
    }

    @Test
    void 페이지_경계와_마지막_페이지가_올바르다() {
        Pageable firstPage = PageRequest.of(0, 2, Sort.by("assetName"));
        Page<TangibleAsset> page0 = tangibleAssetRepository.findAllByContainsKeyword(marker, firstPage);

        assertThat(page0.getContent()).hasSize(2);
        assertThat(page0.getTotalElements()).isEqualTo(5);
        assertThat(page0.getTotalPages()).isEqualTo(3); // 5건을 2건씩 -> 3페이지
        assertThat(page0.hasNext()).isTrue();

        Page<TangibleAsset> lastPage = tangibleAssetRepository.findAllByContainsKeyword(marker, firstPage.next().next());
        assertThat(lastPage.getContent()).hasSize(1); // 마지막 페이지는 1건만 남는다
        assertThat(lastPage.hasNext()).isFalse();
    }

    @Test
    void 존재하지_않는_키워드는_빈_결과를_반환한다() {
        Page<TangibleAsset> page = tangibleAssetRepository.findAllByContainsKeyword(
                marker + "-없는값-XYZ", PageRequest.of(0, 10));

        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isZero();
        assertThat(page.getTotalPages()).isZero();
    }
}
