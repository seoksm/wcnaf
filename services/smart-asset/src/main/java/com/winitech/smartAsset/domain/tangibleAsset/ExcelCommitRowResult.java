package com.winitech.smartAsset.domain.tangibleAsset;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/** S-213 엑셀 확정의 행 1건 처리 결과 - excel_commit_row에 저장되는 값이자 처리 직후의 반환값이기도 하다 */
@Getter
@Builder
public class ExcelCommitRowResult {

    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    private String status;
    /** CREATED | UPDATED - status가 COMPLETED일 때만 값이 있다 */
    private String resultAction;
    private UUID tangibleAssetId;
    /** status가 FAILED일 때만 값이 있다 */
    private String errorMessage;

    public boolean isSuccess() {
        return STATUS_COMPLETED.equals(status);
    }

    public static ExcelCommitRowResult success(String resultAction, UUID tangibleAssetId) {
        return ExcelCommitRowResult.builder()
                .status(STATUS_COMPLETED)
                .resultAction(resultAction)
                .tangibleAssetId(tangibleAssetId)
                .build();
    }

    public static ExcelCommitRowResult failure(String errorMessage) {
        return ExcelCommitRowResult.builder()
                .status(STATUS_FAILED)
                .errorMessage(errorMessage)
                .build();
    }
}
