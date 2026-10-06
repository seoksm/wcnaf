package com.winitech.smartAsset.domain.depreciation;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** S-230 상단 총액법 요약 - 회계팀이 재무제표에 옮겨 적는 숫자 */
@Getter
public class DepreciationSummaryInfo {

    private final BigDecimal totalAcquisitionAmount;
    private final BigDecimal totalOpeningAccumulated;
    private final BigDecimal totalPeriodDepreciation;
    private final BigDecimal totalClosingAccumulated;
    private final BigDecimal totalBookValue;
    private final long excludedCount;
    private final boolean confirmed;
    private final OffsetDateTime confirmedAt;
    private final OffsetDateTime calculatedAt;

    public DepreciationSummaryInfo(List<DepreciationRowInfo> rows, boolean confirmed, OffsetDateTime confirmedAt) {
        this.totalAcquisitionAmount = sum(rows, DepreciationRowInfo::getAcquisitionAmount);
        this.totalOpeningAccumulated = sum(rows, DepreciationRowInfo::getOpeningAccumulated);
        this.totalPeriodDepreciation = sum(rows, DepreciationRowInfo::getPeriodDepreciation);
        this.totalClosingAccumulated = sum(rows, DepreciationRowInfo::getClosingAccumulated);
        this.totalBookValue = sum(rows, DepreciationRowInfo::getBookValue);
        this.excludedCount = rows.stream().filter(r -> r.getExcludedReason() != null).count();
        this.confirmed = confirmed;
        this.confirmedAt = confirmedAt;
        this.calculatedAt = confirmed ? null : OffsetDateTime.now();
    }

    private static BigDecimal sum(List<DepreciationRowInfo> rows, java.util.function.Function<DepreciationRowInfo, BigDecimal> getter) {
        return rows.stream()
                .map(getter)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
