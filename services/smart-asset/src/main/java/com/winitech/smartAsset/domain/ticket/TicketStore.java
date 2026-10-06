package com.winitech.smartAsset.domain.ticket;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;

import java.util.UUID;

public interface TicketStore {

    Ticket store(Ticket ticket);

    void modify(Ticket ticket, String title, String content);

    void assign(Ticket ticket, UUID assigneeId);

    void moveStatus(Ticket ticket, Ticket.Status status);

    void complete(Ticket ticket, TangibleAsset createdAsset);
}
