package com.winitech.smartAsset.domain.ticket;

import java.util.List;
import java.util.UUID;

public interface TicketCommentReader {

    List<TicketComment> findAllByTicketId(UUID ticketId);
}
