package com.winitech.smartAsset.infrastructure.ticket;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.ticket.Ticket;
import com.winitech.smartAsset.domain.ticket.TicketStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TicketStoreImpl implements TicketStore {

    private final TicketRepository ticketRepository;

    @Override
    public Ticket store(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void modify(Ticket ticket, String title, String content) {
        ticket.modify(title, content);
    }

    @Override
    public void assign(Ticket ticket, UUID assigneeId) {
        ticket.assign(assigneeId);
    }

    @Override
    public void moveStatus(Ticket ticket, Ticket.Status status) {
        ticket.moveStatus(status);
    }

    @Override
    public void complete(Ticket ticket, TangibleAsset createdAsset) {
        ticket.complete(createdAsset);
    }
}
