package com.winitech.smartAsset.infrastructure.ticket;

import com.winitech.smartAsset.domain.ticket.TicketComment;
import com.winitech.smartAsset.domain.ticket.TicketCommentStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TicketCommentStoreImpl implements TicketCommentStore {

    private final TicketCommentRepository ticketCommentRepository;

    @Override
    public TicketComment store(TicketComment comment) {
        return ticketCommentRepository.save(comment);
    }
}
