package com.winitech.smartAsset.domain.tangibleAsset;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** S-213 엑셀 업서트 확정 결과 요약 */
@Getter
@Builder
public class TangibleAssetExcelCommitResult {

    private int createdCount;
    private int updatedCount;
    private int skippedCount;

    /** 확정 단계에서 (재검증 실패 등으로) 실제로 반영되지 못한 행들 */
    private List<TangibleAssetExcelRow> failedRows;
}
