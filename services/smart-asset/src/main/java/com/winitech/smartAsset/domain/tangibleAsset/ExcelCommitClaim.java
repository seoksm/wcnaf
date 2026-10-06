package com.winitech.smartAsset.domain.tangibleAsset;

import lombok.Builder;
import lombok.Getter;

/**
 * S-213 엑셀 확정(commit) 요청 레벨 멱등성 판단 결과.
 * ExcelCommitIdempotencyStore.claim()이 이 네 가지 중 하나로 이번 호출자가 무엇을 해야 하는지 알려준다.
 */
@Getter
@Builder
public class ExcelCommitClaim {

    public enum Outcome {
        /** 처음 보는 commitId이거나, 같은 내용의 요청이 이전에 죽었다(lease 만료/FAILED) - 이번 호출자가 처리한다 */
        CLAIMED,
        /** 같은 내용의 요청이 이미 끝났다 - 그 결과를 그대로 반환한다 */
        ALREADY_COMPLETED,
        /** 같은 내용의 요청이 지금 유효하게 처리 중이다(다른 스레드/인스턴스) - 잠시 후 다시 확인해야 한다 */
        IN_PROGRESS_ELSEWHERE,
        /** 같은 commitId인데 요청 내용(행 목록)이 다르다 - 거부한다 */
        REQUEST_MISMATCH
    }

    private Outcome outcome;
    private TangibleAssetExcelCommitResult completedResult;

    public static ExcelCommitClaim claimed() {
        return ExcelCommitClaim.builder().outcome(Outcome.CLAIMED).build();
    }

    public static ExcelCommitClaim alreadyCompleted(TangibleAssetExcelCommitResult result) {
        return ExcelCommitClaim.builder().outcome(Outcome.ALREADY_COMPLETED).completedResult(result).build();
    }

    public static ExcelCommitClaim inProgressElsewhere() {
        return ExcelCommitClaim.builder().outcome(Outcome.IN_PROGRESS_ELSEWHERE).build();
    }

    public static ExcelCommitClaim requestMismatch() {
        return ExcelCommitClaim.builder().outcome(Outcome.REQUEST_MISMATCH).build();
    }
}
