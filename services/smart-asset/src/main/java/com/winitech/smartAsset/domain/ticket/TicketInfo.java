package com.winitech.smartAsset.domain.ticket;

import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class TicketInfo {

    private final UUID ticketId;
    private final Ticket.Type ticketType;
    private final String title;
    private final String content;
    private final Ticket.Status status;
    private final UUID requestedBy;
    private final UUID assigneeId;
    private final boolean unassigned;
    private final UUID tangibleAssetId;
    private final String tangibleAssetCode;
    private final String tangibleAssetName;
    private final UUID licenseAssignedUserId;
    private final UUID createdAssetId;
    private final LocalDate targetDueDate;
    private final boolean overdue;
    private final OffsetDateTime completedAt;
    private final OffsetDateTime createAt;

    public TicketInfo(Ticket ticket) {
        this.ticketId = ticket.getId();
        this.ticketType = ticket.getTicketType();
        this.title = ticket.getTitle();
        this.content = ticket.getContent();
        this.status = ticket.getStatus();
        this.requestedBy = ticket.getRequestedBy();
        this.assigneeId = ticket.getAssigneeId();
        this.unassigned = ticket.getAssigneeId() == null;
        this.tangibleAssetId = ticket.getTangibleAsset() != null ? ticket.getTangibleAsset().getId() : null;
        this.tangibleAssetCode = ticket.getTangibleAsset() != null ? ticket.getTangibleAsset().getAssetCode() : null;
        this.tangibleAssetName = ticket.getTangibleAsset() != null ? ticket.getTangibleAsset().getAssetName() : null;
        this.licenseAssignedUserId = ticket.getLicenseAssignedUser() != null ? ticket.getLicenseAssignedUser().getId() : null;
        this.createdAssetId = ticket.getCreatedAsset() != null ? ticket.getCreatedAsset().getId() : null;
        this.targetDueDate = ticket.getTargetDueDate();
        this.overdue = ticket.isOverdue();
        this.completedAt = ticket.getCompletedAt();
        this.createAt = ticket.getCreateAt();
    }
}
