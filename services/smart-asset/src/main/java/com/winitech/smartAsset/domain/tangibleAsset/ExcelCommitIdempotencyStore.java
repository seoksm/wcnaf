package com.winitech.smartAsset.domain.tangibleAsset;

import java.util.UUID;

/**
 * S-213 엑셀 업서트 확정(commit)의 요청 레벨 중복 처리를 막는 멱등성 저장소 포트.
 * DB에 기록해 여러 인스턴스·재시작 환경에서도 같은 commitId 요청이 두 번 반영되지 않게 한다.
 */
public interface ExcelCommitIdempotencyStore {

    /**
     * commitId(+ 요청 내용 지문 requestHash)로 이번 호출자가 처리를 진행해야 하는지 판단한다.
     * 처음 보는 commitId이거나, 같은 내용의 이전 시도가 죽었으면(lease 만료 또는 FAILED) 이번
     * 호출자가 새로 claim한다.
     */
    ExcelCommitClaim claim(UUID commitId, UUID requestedBy, String requestHash);

    void complete(UUID commitId, TangibleAssetExcelCommitResult result);

    /** 요청 레벨에서 예상하지 못한 예외로 중단된 경우 - 무조건 COMPLETED로 남기지 않고 FAILED로
     * 기록해, 다음 재시도가 안전하게 재claim할 수 있게 한다. */
    void fail(UUID commitId, String errorMessage);
}
