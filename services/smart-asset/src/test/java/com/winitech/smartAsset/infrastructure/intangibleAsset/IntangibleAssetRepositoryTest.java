package com.winitech.smartAsset.infrastructure.intangibleAsset;

import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAsset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GET /intangible-asset (withinDays=90) 호출 시 PostgreSQL이
 * "could not determine data type of parameter $3"(SQLState 42P18)로 실패하던 버그의 회귀
 * 테스트. 원인은 {@code (:expiryBefore IS NULL OR a.expiryDate <= :expiryBefore)} 처럼
 * 선택 필터 파라미터가 IS NULL 위치에서만 쓰이면 Postgres가 prepared statement 파싱 단계에서
 * 타입을 정하지 못하는 것이었다(AssetHistoryRepository의 동일 문제 주석 참고). 수정 후에는
 * 조건 조합별로 쿼리 자체가 나뉘어(IntangibleAssetRepository) null 바인딩에 의존하지 않는다.
 * 실제 Postgres에 붙는 통합 테스트로, 다른 테스트/운영 데이터와 섞이지 않도록 고유 마커를
 * 자산명에 넣는다.
 */
@SpringBootTest
@Transactional
class IntangibleAssetRepositoryTest {

    @Autowired
    private IntangibleAssetRepository intangibleAssetRepository;

    private String marker;
    private LocalDate baseDate;
    private IntangibleAsset assetBeforeBase;
    private IntangibleAsset assetOnBase;
    private IntangibleAsset assetAfterBase;
    private IntangibleAsset assetDisabled;

    @BeforeEach
    void setUp() {
        marker = "INTANGIBLE-TEST-" + UUID.randomUUID().toString().substring(0, 8);
        baseDate = LocalDate.now();

        assetBeforeBase = intangibleAssetRepository.save(IntangibleAsset.builder()
                .intangibleType(IntangibleAsset.Type.CERTIFICATE)
                .name(marker + "-A-cert")
                .expiryDate(baseDate.minusDays(10))
                .build());

        assetOnBase = intangibleAssetRepository.save(IntangibleAsset.builder()
                .intangibleType(IntangibleAsset.Type.CERTIFICATE)
                .name(marker + "-B-cert")
                .expiryDate(baseDate)
                .build());

        assetAfterBase = intangibleAssetRepository.save(IntangibleAsset.builder()
                .intangibleType(IntangibleAsset.Type.DOMAIN)
                .name(marker + "-C-domain")
                .expiryDate(baseDate.plusDays(5))
                .build());

        assetDisabled = intangibleAssetRepository.save(IntangibleAsset.builder()
                .intangibleType(IntangibleAsset.Type.CERTIFICATE)
                .name(marker + "-D-cert")
                .expiryDate(baseDate.minusDays(1))
                .build());
        assetDisabled.delete();
        intangibleAssetRepository.save(assetDisabled);
    }

    private Set<String> namesOf(List<IntangibleAsset> content) {
        return content.stream().map(IntangibleAsset::getName)
                .filter(name -> name.startsWith(marker))
                .collect(Collectors.toSet());
    }

    @Test
    void keyword_없이_expiryBefore만_주면_그_날짜까지_만료되는_ENABLE_자산만_조회된다() {
        Page<IntangibleAsset> page = intangibleAssetRepository.findAllEnabledByExpiryBefore(
                baseDate, PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "expiryDate")));

        Set<String> names = namesOf(page.getContent());

        assertThat(names)
                .as("기준일 이전(assetBeforeBase)과 기준일과 정확히 같은 만료일(assetOnBase)은 포함되어야 한다")
                .contains(assetBeforeBase.getName(), assetOnBase.getName());
        assertThat(names)
                .as("기준일 이후 만료(assetAfterBase)는 제외되어야 한다")
                .doesNotContain(assetAfterBase.getName());
        assertThat(names)
                .as("DISABLE 상태(assetDisabled)는 만료일 조건과 무관하게 항상 제외되어야 한다")
                .doesNotContain(assetDisabled.getName());
    }

    @Test
    void keyword와_expiryBefore가_모두_있으면_두_조건이_함께_적용되고_페이지_content와_전체건수가_일치한다() {
        Page<IntangibleAsset> firstPage = intangibleAssetRepository.findAllEnabledByKeywordAndExpiryBefore(
                marker, baseDate, PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "expiryDate")));

        // marker는 이번 테스트에서만 생성한 무작위 문자열이라, marker로 검색하면 정확히
        // assetBeforeBase·assetOnBase 두 건만 매치되어야 한다(assetAfterBase는 만료일 조건에서,
        // assetDisabled는 status 조건에서 각각 제외된다).
        assertThat(firstPage.getTotalElements()).isEqualTo(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getContent()).hasSize(1);

        Page<IntangibleAsset> secondPage = intangibleAssetRepository.findAllEnabledByKeywordAndExpiryBefore(
                marker, baseDate, PageRequest.of(1, 1, Sort.by(Sort.Direction.ASC, "expiryDate")));
        assertThat(secondPage.getContent()).hasSize(1);

        Set<String> allNames = namesOf(firstPage.getContent());
        allNames.addAll(namesOf(secondPage.getContent()));
        assertThat(allNames).containsExactlyInAnyOrder(assetBeforeBase.getName(), assetOnBase.getName());
    }

    @Test
    void keyword와_expiryBefore가_모두_없으면_ENABLE_전체가_조회된다() {
        Page<IntangibleAsset> page = intangibleAssetRepository.findAllEnabled(
                PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "expiryDate")));

        Set<String> names = namesOf(page.getContent());

        assertThat(names).containsExactlyInAnyOrder(
                assetBeforeBase.getName(), assetOnBase.getName(), assetAfterBase.getName());
        assertThat(names).doesNotContain(assetDisabled.getName());
    }

    @Test
    void keyword_부분검색은_일치하는_이름만_반환한다() {
        Page<IntangibleAsset> page = intangibleAssetRepository.findAllEnabledByKeyword(
                marker + "-B", PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "expiryDate")));

        Set<String> names = namesOf(page.getContent());

        assertThat(names).containsExactly(assetOnBase.getName());
    }
}
