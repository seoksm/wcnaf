package com.winitech.smartAsset.domain.acknowledgement;

import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/** S-431 확인서 현황 · S-440 목록의 행 1개 */
@Getter
public class AcknowledgementInfo {

    private final UUID acknowledgementId;
    private final UUID tangibleAssetId;
    private final String assetCode;
    private final String assetName;
    private final String categoryName;
    private final UUID memberId;
    private final Acknowledgement.Type type;
    private final Acknowledgement.Status status;
    private final LocalDate dueDate;
    private final UUID requestedBy;
    private final OffsetDateTime requestedAt;
    private final boolean overdue;

    public AcknowledgementInfo(Acknowledgement ack) {
        this.acknowledgementId = ack.getId();
        this.tangibleAssetId = ack.getTangibleAsset().getId();
        this.assetCode = ack.getTangibleAsset().getAssetCode();
        this.assetName = ack.getTangibleAsset().getAssetName();
        this.categoryName = ack.getTangibleAsset().getCategory() != null ? ack.getTangibleAsset().getCategory().getCategoryName() : null;
        this.memberId = ack.getMemberId();
        this.type = ack.getType();
        this.status = ack.getStatus();
        this.dueDate = ack.getDueDate();
        this.requestedBy = ack.getRequestedBy();
        this.requestedAt = ack.getRequestedAt();
        this.overdue = ack.isOverdue();
    }
}
