package com.winitech.smartAsset.domain.depreciation;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Q-21 감가상각 계산 규칙 (설계문서 09 §1) - 이벤트 기반, 정액법, 월할(취득월 포함), 비망가액 유지.
 * 배치 없이 조회 시점에 항상 다시 계산할 수 있는 순수 함수로 구성한다.
 */
public final class DepreciationCalculator {

    private DepreciationCalculator() {
    }

    public enum ExcludedReason {
        /** D5: 취득가액이 0/미입력 */
        NO_AMOUNT,
        /** D6: 자산 종류에 감가상각 기준(내용연수)이 설정되지 않음 */
        NO_BASIS,
        /**
         * @deprecated S-240 구현 전에는 생애상태가 불용/처분완료면 전부 제외 처리했으나, 이제
         * calculate()가 불용/처분완료 시점(lifeStatusChangedAt)까지만 상각하고 그 이후는 동결하는
         * 방식으로 대체했다(D4) - 더 이상 이 사유로 제외하지 않는다. 이미 확정된 과거
         * depreciation_snapshot 행에 이 값이 저장되어 있을 수 있어(호환성) 열거값 자체는 남겨둔다.
         */
        @Deprecated
        NOT_DEPRECIABLE_STATUS,
    }

    @Getter
    @Builder
    public static class Result {
        private BigDecimal openingAccumulated;
        private BigDecimal periodDepreciation;
        private BigDecimal closingAccumulated;
        private BigDecimal bookValue;
        private int elapsedMonths;
        private int usefulLifeMonths;
    }

    public static Optional<ExcludedReason> excludedReason(TangibleAsset asset, AssetCategory category) {
        // 불용/처분완료는 더 이상 여기서 제외하지 않는다 - calculate()가 lifeStatusChangedAt(동결
        // 시점)까지만 상각하고 그 이후는 자연스럽게 0으로 수렴하므로, 정상적으로 계산 가능하다(D4).
        if (asset.getAcquisitionAmount() == null || asset.getAcquisitionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.of(ExcludedReason.NO_AMOUNT);
        }
        if (category == null || category.getUsefulLifeMonths() == null || category.getUsefulLifeMonths() <= 0) {
            return Optional.of(ExcludedReason.NO_BASIS);
        }
        return Optional.empty();
    }

    /**
     * @param periodStartMonth 회계연도 1월
     * @param periodEndMonth   조회 분기의 마지막 월 (Q1=3월, Q2=6월, Q3=9월, Q4=12월)
     */
    public static Result calculate(TangibleAsset asset, AssetCategory category, YearMonth periodStartMonth, YearMonth periodEndMonth) {
        BigDecimal acquisitionAmount = asset.getAcquisitionAmount();
        BigDecimal memorandumValue = category.getMemorandumValue() != null ? category.getMemorandumValue() : BigDecimal.ZERO;
        BigDecimal residualRate = category.getResidualRate() != null ? category.getResidualRate() : BigDecimal.ZERO;
        int usefulLifeMonths = category.getUsefulLifeMonths();

        // 잔존가치(회계상 표준 개념, 보통 0%) + 비망가액(실무 floor, 기본 1,000원)을 모두 상각대상액에서 뺀다.
        // 잔존가치율 0%인 경우 "월 상각액 = (취득가액 - 비망가액) ÷ 내용연수" (설계문서 §1-③ 예시)와 정확히 일치한다.
        BigDecimal salvageValue = acquisitionAmount.multiply(residualRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal depreciableBase = acquisitionAmount.subtract(salvageValue).subtract(memorandumValue).max(BigDecimal.ZERO);
        BigDecimal monthly = depreciableBase.divide(BigDecimal.valueOf(usefulLifeMonths), 2, RoundingMode.HALF_UP);

        YearMonth acquisitionMonth = YearMonth.from(asset.getAcquisitionDate());
        YearMonth openingAsOf = periodStartMonth.minusMonths(1);

        int openingElapsed = elapsedMonths(acquisitionMonth, freezeIfNeeded(asset, openingAsOf), usefulLifeMonths);
        int closingElapsed = elapsedMonths(acquisitionMonth, freezeIfNeeded(asset, periodEndMonth), usefulLifeMonths);

        BigDecimal openingAccumulated = monthly.multiply(BigDecimal.valueOf(openingElapsed)).min(depreciableBase);
        BigDecimal closingAccumulated = monthly.multiply(BigDecimal.valueOf(closingElapsed)).min(depreciableBase);
        BigDecimal periodDepreciation = closingAccumulated.subtract(openingAccumulated);
        BigDecimal bookValue = acquisitionAmount.subtract(closingAccumulated);

        return Result.builder()
                .openingAccumulated(openingAccumulated)
                .periodDepreciation(periodDepreciation)
                .closingAccumulated(closingAccumulated)
                .bookValue(bookValue)
                .elapsedMonths(closingElapsed)
                .usefulLifeMonths(usefulLifeMonths)
                .build();
    }

    /** D4: 불용/처분완료 상태는 그 상태가 된 달(lifeStatusChangedAt)까지만 상각하고 다음 달부터
     * 멈춘다 - 조회하려는 월이 동결 시점보다 나중이면 동결 시점으로 고정해서 돌려준다. 동결 시점
     * "이전"을 조회하는 경우(예: 불용 처리 이전 분기 조회)는 그대로 둔다 - 그때는 아직 정상 상각
     * 중이었으므로 동결할 이유가 없다. */
    private static YearMonth freezeIfNeeded(TangibleAsset asset, YearMonth requestedMonth) {
        boolean frozen = asset.getLifeStatus() == TangibleAsset.LifeStatus.DISUSE
                || asset.getLifeStatus() == TangibleAsset.LifeStatus.DISPOSED;
        if (!frozen || asset.getLifeStatusChangedAt() == null) {
            return requestedMonth;
        }
        YearMonth freezeMonth = YearMonth.from(asset.getLifeStatusChangedAt());
        return freezeMonth.isBefore(requestedMonth) ? freezeMonth : requestedMonth;
    }

    /** 취득월을 포함해 asOfMonth까지 경과한 개월수, 내용연수를 넘지 않도록 상한 적용 (D4, 월할 상각) */
    private static int elapsedMonths(YearMonth acquisitionMonth, YearMonth asOfMonth, int usefulLifeMonths) {
        if (acquisitionMonth.isAfter(asOfMonth)) {
            return 0;
        }
        long months = ChronoUnit.MONTHS.between(acquisitionMonth, asOfMonth) + 1;
        return (int) Math.max(0, Math.min(months, usefulLifeMonths));
    }
}
