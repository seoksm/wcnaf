package com.winitech.smartAsset.domain.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TicketReader {

    Ticket findById(UUID ticketId);

    /** S-600 칸반 - 진행 중 전건 + 완료는 doneSince 이후분만(설계문서 "완료 컬럼은 이번 주 것만") */
    List<Ticket> findKanbanBoard(OffsetDateTime doneSince);

    Page<Ticket> findAll(String keyword, Ticket.Status status, Pageable pageable);

    /** S-611 내 티켓 */
    List<Ticket> findAllByRequestedBy(UUID requestedBy);

    /** S-700 티켓 현황 KPI */
    long countByStatusIn(Collection<Ticket.Status> statuses);

    /** S-700 티켓 현황 KPI - Q-51 SLA 초과 */
    long countOverdue(LocalDate today);

    /** S-700 티켓 현황 KPI - 이번 주 완료 */
    long countCompletedSince(OffsetDateTime since);
}
