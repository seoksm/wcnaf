package com.winitech.smartAsset.domain.depreciation;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * D4 동결 규칙(불용/처분완료 상태는 그 상태가 된 달까지만 상각하고 다음 달부터 멈춘다) 검증.
 * TangibleAsset의 공개 API(disuse/dispose 등)는 lifeStatusChangedAt을 항상 "지금"으로 세팅하므로,
 * 과거의 특정 시점에 동결된 상황을 재현하려면 리플렉션으로 그 필드를 직접 주입해야 한다.
 */
class DepreciationCalculatorTest {

    private AssetCategory category() {
        return AssetCategory.builder()
                .categoryCode("NB")
                .categoryName("노트북")
                .usefulLifeMonths(36)
                .residualRate(BigDecimal.ZERO)
                .memorandumValue(BigDecimal.valueOf(1000))
                .build();
    }

    private TangibleAsset asset(TangibleAsset.LifeStatus lifeStatus, YearMonth lifeStatusChangedAt) {
        TangibleAsset asset = TangibleAsset.builder()
                .assetCode("AST-2026-0001")
                .assetName("노트북")
                .lifeStatus(lifeStatus)
                .assignType(TangibleAsset.AssignType.UNASSIGNED)
                .acquisitionDate(LocalDate.of(2025, 1, 1))
                .acquisitionAmount(new BigDecimal("3601000"))
                .build();
        if (lifeStatusChangedAt != null) {
            ReflectionTestUtils.setField(asset, "lifeStatusChangedAt",
                    lifeStatusChangedAt.atDay(15).atStartOfDay().atOffset(ZoneOffset.UTC));
        }
        return asset;
    }

    @Test
    void 정상_사용중인_자산은_제외되지_않는다() {
        TangibleAsset asset = asset(TangibleAsset.LifeStatus.USE, null);

        Optional<DepreciationCalculator.ExcludedReason> reason = DepreciationCalculator.excludedReason(asset, category());

        assertThat(reason).isEmpty();
    }

    @Test
    void 불용_처분완료_상태여도_더이상_제외_사유가_아니다() {
        TangibleAsset disused = asset(TangibleAsset.LifeStatus.DISUSE, YearMonth.of(2026, 3));
        TangibleAsset disposed = asset(TangibleAsset.LifeStatus.DISPOSED, YearMonth.of(2026, 3));

        assertThat(DepreciationCalculator.excludedReason(disused, category())).isEmpty();
        assertThat(DepreciationCalculator.excludedReason(disposed, category())).isEmpty();
    }

    @Test
    void 동결_시점_이전_분기는_정상적으로_상각된다() {
        // 취득 2025-01, 불용 전환 2026-03 -> Q1(2026-01~03) 조회는 동결 시점(3월) 이전 구간이 아니라
        // 동결 시점을 "포함"하므로, 3월까지는 정상적으로 상각되어야 한다(그 달까지는 상각, D4).
        TangibleAsset asset = asset(TangibleAsset.LifeStatus.DISUSE, YearMonth.of(2026, 3));

        DepreciationCalculator.Result q1 = DepreciationCalculator.calculate(
                asset, category(), YearMonth.of(2026, 1), YearMonth.of(2026, 3));

        // 취득월(2025-01) 포함 2026-03까지 15개월 경과
        assertThat(q1.getElapsedMonths()).isEqualTo(15);
        assertThat(q1.getPeriodDepreciation()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void 동결_시점_이후_분기는_추가로_상각되지_않는다() {
        // periodDepreciation은 분기 단독분이 아니라 회계연도 누적(YTD) 값이므로, 불용 전환(2026-03)
        // 이후 분기(Q4=12월 조회)라도 동결 시점이 속한 Q1과 누적상각액이 완전히 같아야 한다 -
        // 즉 3월 이후로는 한 푼도 더 상각되지 않는다는 뜻이다.
        TangibleAsset asset = asset(TangibleAsset.LifeStatus.DISUSE, YearMonth.of(2026, 3));
        AssetCategory category = category();

        DepreciationCalculator.Result q1 = DepreciationCalculator.calculate(
                asset, category, YearMonth.of(2026, 1), YearMonth.of(2026, 3));
        DepreciationCalculator.Result q4 = DepreciationCalculator.calculate(
                asset, category, YearMonth.of(2026, 1), YearMonth.of(2026, 12));

        assertThat(q4.getElapsedMonths()).isEqualTo(q1.getElapsedMonths());
        assertThat(q4.getPeriodDepreciation()).isEqualByComparingTo(q1.getPeriodDepreciation());
        assertThat(q4.getClosingAccumulated()).isEqualByComparingTo(q1.getClosingAccumulated());
        assertThat(q4.getBookValue()).isEqualByComparingTo(q1.getBookValue());
    }

    @Test
    void 처분완료도_동결_시점_이후에는_동일하게_상각이_멈춘다() {
        TangibleAsset asset = asset(TangibleAsset.LifeStatus.DISPOSED, YearMonth.of(2026, 3));
        AssetCategory category = category();

        DepreciationCalculator.Result q3 = DepreciationCalculator.calculate(
                asset, category, YearMonth.of(2026, 1), YearMonth.of(2026, 9));
        DepreciationCalculator.Result q4 = DepreciationCalculator.calculate(
                asset, category, YearMonth.of(2026, 1), YearMonth.of(2026, 12));

        assertThat(q4.getBookValue()).isEqualByComparingTo(q3.getBookValue());
    }

    @Test
    void lifeStatusChangedAt이_없으면_동결하지_않는다() {
        // 과거에 생성된 데이터 등 lifeStatusChangedAt이 비어있는 극단적 상황에서도 계산이 죽지 않고
        // 동결 없이 그대로 진행되어야 한다(freezeIfNeeded의 null 가드).
        TangibleAsset asset = asset(TangibleAsset.LifeStatus.DISUSE, null);
        ReflectionTestUtils.setField(asset, "lifeStatusChangedAt", (OffsetDateTime) null);

        DepreciationCalculator.Result result = DepreciationCalculator.calculate(
                asset, category(), YearMonth.of(2026, 1), YearMonth.of(2026, 3));

        assertThat(result.getElapsedMonths()).isEqualTo(15);
    }
}
