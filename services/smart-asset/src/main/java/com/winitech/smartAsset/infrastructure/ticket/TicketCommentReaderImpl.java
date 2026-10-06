package com.winitech.smartAsset.infrastructure.ticket;

import com.winitech.smartAsset.domain.ticket.TicketComment;
import com.winitech.smartAsset.domain.ticket.TicketCommentReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TicketCommentReaderImpl implements TicketCommentReader {

    private final TicketCommentRepository ticketCommentRepository;

    @Override
    public List<TicketComment> findAllByTicketId(UUID ticketId) {
        return ticketCommentRepository.findAllByTicket_IdOrderByCreateAtAsc(ticketId);
    }
}
