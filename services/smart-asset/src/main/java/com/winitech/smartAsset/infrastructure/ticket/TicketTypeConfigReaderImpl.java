package com.winitech.smartAsset.infrastructure.ticket;

import com.winitech.smartAsset.domain.ticket.Ticket;
import com.winitech.smartAsset.domain.ticket.TicketTypeConfig;
import com.winitech.smartAsset.domain.ticket.TicketTypeConfigReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TicketTypeConfigReaderImpl implements TicketTypeConfigReader {

    private final TicketTypeConfigRepository ticketTypeConfigRepository;

    @Override
    public TicketTypeConfig findByTicketType(Ticket.Type ticketType) {
        return ticketTypeConfigRepository.findByTicketType(ticketType).orElseThrow();
    }
}
