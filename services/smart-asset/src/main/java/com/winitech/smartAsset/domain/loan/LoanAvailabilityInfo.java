package com.winitech.smartAsset.domain.loan;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * S-420 대여 가능 자산 목록의 행 1개 - LOANABLE(대여가능)이든 ON_LOAN(대여중)이든 목록에 함께
 * 남긴다. "다른 사람이 대여 중인 자산도 목록에 남기고 대여자를 표시한다" (설계문서 §3) - 숨기면
 * "창고에 있을 텐데 왜 목록에 없나"가 되기 때문.
 */
@Getter
public class LoanAvailabilityInfo {

    private final UUID tangibleAssetId;
    private final String assetCode;
    private final String assetName;
    private final String categoryName;
    private final boolean available;
    private final UUID currentMemberId;
    private final LocalDate dueDate;

    public LoanAvailabilityInfo(TangibleAsset asset, Loan activeLoan) {
        this.tangibleAssetId = asset.getId();
        this.assetCode = asset.getAssetCode();
        this.assetName = asset.getAssetName();
        this.categoryName = asset.getCategory() != null ? asset.getCategory().getCategoryName() : null;
        this.available = activeLoan == null;
        this.currentMemberId = activeLoan != null ? activeLoan.getMemberId() : null;
        this.dueDate = activeLoan != null ? activeLoan.getDueDate() : null;
    }
}
