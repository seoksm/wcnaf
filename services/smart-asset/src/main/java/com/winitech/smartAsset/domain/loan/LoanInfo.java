package com.winitech.smartAsset.domain.loan;

import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/** S-410/411/420/421 대여 목록·상세 행 1개 */
@Getter
public class LoanInfo {

    private final UUID loanId;
    private final UUID tangibleAssetId;
    private final String assetCode;
    private final String assetName;
    private final String categoryName;
    private final UUID memberId;
    private final Loan.Status status;
    private final LocalDate dueDate;
    private final Integer extendCount;
    private final OffsetDateTime borrowedAt;
    private final UUID approvedBy;
    private final OffsetDateTime approvedAt;
    private final String rejectReason;
    private final OffsetDateTime returnedAt;
    private final Loan.ReturnCondition returnCondition;
    private final UUID createdBy;
    private final boolean overdue;

    public LoanInfo(Loan loan) {
        this.loanId = loan.getId();
        this.tangibleAssetId = loan.getTangibleAsset().getId();
        this.assetCode = loan.getTangibleAsset().getAssetCode();
        this.assetName = loan.getTangibleAsset().getAssetName();
        this.categoryName = loan.getTangibleAsset().getCategory() != null
                ? loan.getTangibleAsset().getCategory().getCategoryName() : null;
        this.memberId = loan.getMemberId();
        this.status = loan.getStatus();
        this.dueDate = loan.getDueDate();
        this.extendCount = loan.getExtendCount();
        this.borrowedAt = loan.getBorrowedAt();
        this.approvedBy = loan.getApprovedBy();
        this.approvedAt = loan.getApprovedAt();
        this.rejectReason = loan.getRejectReason();
        this.returnedAt = loan.getReturnedAt();
        this.returnCondition = loan.getReturnCondition();
        this.createdBy = loan.getCreatedBy();
        this.overdue = loan.isOverdue();
    }
}
