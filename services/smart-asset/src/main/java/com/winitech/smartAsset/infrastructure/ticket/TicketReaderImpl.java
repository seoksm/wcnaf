package com.winitech.smartAsset.infrastructure.ticket;

import com.winitech.smartAsset.domain.ticket.Ticket;
import com.winitech.smartAsset.domain.ticket.TicketReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TicketReaderImpl implements TicketReader {

    private final TicketRepository ticketRepository;

    @Override
    public Ticket findById(UUID ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow();
    }

    @Override
    public List<Ticket> findKanbanBoard(OffsetDateTime doneSince) {
        return ticketRepository.findKanbanBoard(doneSince);
    }

    @Override
    public Page<Ticket> findAll(String keyword, Ticket.Status status, Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (hasKeyword && status != null) {
            return ticketRepository.findAllByKeywordAndStatus(keyword, status, pageable);
        }
        if (hasKeyword) {
            return ticketRepository.findAllByKeyword(keyword, pageable);
        }
        if (status != null) {
            return ticketRepository.findAllByStatus(status, pageable);
        }
        return ticketRepository.findAllTickets(pageable);
    }

    @Override
    public List<Ticket> findAllByRequestedBy(UUID requestedBy) {
        return ticketRepository.findAllByRequestedByOrderByCreateAtDesc(requestedBy);
    }

    @Override
    public long countByStatusIn(Collection<Ticket.Status> statuses) {
        return ticketRepository.countByStatusIn(statuses);
    }

    @Override
    public long countOverdue(LocalDate today) {
        return ticketRepository.countOverdue(today);
    }

    @Override
    public long countCompletedSince(OffsetDateTime since) {
        return ticketRepository.countCompletedSince(since);
    }
}
