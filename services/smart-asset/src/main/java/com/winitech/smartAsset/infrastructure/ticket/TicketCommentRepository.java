package com.winitech.smartAsset.infrastructure.ticket;

import com.winitech.smartAsset.domain.ticket.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketCommentRepository extends JpaRepository<TicketComment, UUID> {

    List<TicketComment> findAllByTicket_IdOrderByCreateAtAsc(UUID ticketId);
}
