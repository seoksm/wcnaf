package com.winitech.smartAsset.domain.ticket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
public class TicketCommand {

    private Ticket.Type ticketType;
    private String title;
    private String content;
    private UUID tangibleAssetId;

    @Getter
    @Builder
    public static class UpdateCommand {
        @Setter
        private UUID ticketId;
        private String title;
        private String content;
    }
}
