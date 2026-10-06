package com.winitech.smartAsset.domain.depreciation;

import lombok.Getter;

import java.math.BigDecimal;

/** S-231 자산별 상각 스케줄의 분기 1행 */
@Getter
public class DepreciationScheduleRowInfo {

    private final int fiscalYear;
    private final String quarter;
    private final BigDecimal openingAccumulated;
    private final BigDecimal periodDepreciation;
    private final BigDecimal closingAccumulated;
    private final BigDecimal bookValue;
    private final boolean confirmed;
    private final boolean excluded;

    private DepreciationScheduleRowInfo(int fiscalYear, DepreciationQuarter quarter, BigDecimal opening, BigDecimal period,
                                         BigDecimal closing, BigDecimal bookValue, boolean confirmed, boolean excluded) {
        this.fiscalYear = fiscalYear;
        this.quarter = quarter.name();
        this.openingAccumulated = opening;
        this.periodDepreciation = period;
        this.closingAccumulated = closing;
        this.bookValue = bookValue;
        this.confirmed = confirmed;
        this.excluded = excluded;
    }

    public static DepreciationScheduleRowInfo excluded(int fiscalYear, DepreciationQuarter quarter) {
        return new DepreciationScheduleRowInfo(fiscalYear, quarter, null, null, null, null, false, true);
    }

    /** 확정된 분기인데 그 분기 스냅샷이 제외 자산이었던 경우 - excluded()와 달리 confirmed=true로 표시한다 */
    public static DepreciationScheduleRowInfo excludedFromSnapshot(int fiscalYear, DepreciationQuarter quarter) {
        return new DepreciationScheduleRowInfo(fiscalYear, quarter, null, null, null, null, true, true);
    }

    public static DepreciationScheduleRowInfo fromCalculation(int fiscalYear, DepreciationQuarter quarter, DepreciationCalculator.Result result) {
        return new DepreciationScheduleRowInfo(fiscalYear, quarter, result.getOpeningAccumulated(), result.getPeriodDepreciation(),
                result.getClosingAccumulated(), result.getBookValue(), false, false);
    }

    public static DepreciationScheduleRowInfo fromSnapshot(int fiscalYear, DepreciationQuarter quarter, DepreciationSnapshot snapshot) {
        return new DepreciationScheduleRowInfo(fiscalYear, quarter, snapshot.getOpeningAccumulated(), snapshot.getPeriodDepreciation(),
                snapshot.getClosingAccumulated(), snapshot.getBookValue(), true, false);
    }
}
