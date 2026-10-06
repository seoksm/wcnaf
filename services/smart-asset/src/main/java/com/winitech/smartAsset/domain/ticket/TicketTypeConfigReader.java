package com.winitech.smartAsset.domain.ticket;

public interface TicketTypeConfigReader {

    TicketTypeConfig findByTicketType(Ticket.Type ticketType);
}
