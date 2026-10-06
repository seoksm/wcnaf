package com.winitech.smartAsset.domain.depreciation;

import lombok.Getter;

import java.util.List;

/** S-230 감가상각 현황 응답 - 자산별 행 + 총액법 요약 */
@Getter
public class DepreciationStatusInfo {

    private final List<DepreciationRowInfo> rows;
    private final DepreciationSummaryInfo summary;

    public DepreciationStatusInfo(List<DepreciationRowInfo> rows, DepreciationSummaryInfo summary) {
        this.rows = rows;
        this.summary = summary;
    }
}
