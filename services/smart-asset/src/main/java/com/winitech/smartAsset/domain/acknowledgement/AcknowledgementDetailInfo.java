package com.winitech.smartAsset.domain.acknowledgement;

import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** S-432(관리자 상세) · S-440(임직원 상세) 공용 상세 정보 */
@Getter
public class AcknowledgementDetailInfo {

    private final UUID acknowledgementId;
    private final UUID tangibleAssetId;
    private final String assetCode;
    private final String assetName;
    private final String categoryName;
    private final UUID memberId;
    private final Acknowledgement.Type type;
    private final String bodySnapshot;
    private final Acknowledgement.Status status;
    private final LocalDate dueDate;
    private final UUID requestedBy;
    private final OffsetDateTime requestedAt;
    private final boolean cancelledYn;
    private final String cancelledReason;
    private final boolean overdue;
    private final List<AcknowledgementApprovalInfo> approvals;

    public AcknowledgementDetailInfo(Acknowledgement ack, List<AcknowledgementApproval> approvals) {
        this.acknowledgementId = ack.getId();
        this.tangibleAssetId = ack.getTangibleAsset().getId();
        this.assetCode = ack.getTangibleAsset().getAssetCode();
        this.assetName = ack.getTangibleAsset().getAssetName();
        this.categoryName = ack.getTangibleAsset().getCategory() != null ? ack.getTangibleAsset().getCategory().getCategoryName() : null;
        this.memberId = ack.getMemberId();
        this.type = ack.getType();
        this.bodySnapshot = ack.getBodySnapshot();
        this.status = ack.getStatus();
        this.dueDate = ack.getDueDate();
        this.requestedBy = ack.getRequestedBy();
        this.requestedAt = ack.getRequestedAt();
        this.cancelledYn = Boolean.TRUE.equals(ack.getCancelledYn());
        this.cancelledReason = ack.getCancelledReason();
        this.overdue = ack.isOverdue();
        this.approvals = approvals.stream().map(AcknowledgementApprovalInfo::new).collect(java.util.stream.Collectors.toList());
    }
}
