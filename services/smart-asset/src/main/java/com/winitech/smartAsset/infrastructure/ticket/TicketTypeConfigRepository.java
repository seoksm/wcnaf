package com.winitech.smartAsset.infrastructure.ticket;

import com.winitech.smartAsset.domain.ticket.Ticket;
import com.winitech.smartAsset.domain.ticket.TicketTypeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketTypeConfigRepository extends JpaRepository<TicketTypeConfig, UUID> {

    Optional<TicketTypeConfig> findByTicketType(Ticket.Type ticketType);
}
