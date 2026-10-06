package com.winitech.smartAsset.domain.ticket;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class TicketCommentInfo {

    private final UUID ticketCommentId;
    private final String content;
    private final UUID writtenBy;
    private final boolean isRequester;
    private final OffsetDateTime createAt;

    public TicketCommentInfo(TicketComment comment) {
        this.ticketCommentId = comment.getId();
        this.content = comment.getContent();
        this.writtenBy = comment.getWrittenBy();
        this.isRequester = Boolean.TRUE.equals(comment.getIsRequester());
        this.createAt = comment.getCreateAt();
    }
}
