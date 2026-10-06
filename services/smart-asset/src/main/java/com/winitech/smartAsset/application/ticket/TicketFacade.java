package com.winitech.smartAsset.application.ticket;

import com.winitech.smartAsset.domain.ticket.Ticket;
import com.winitech.smartAsset.domain.ticket.TicketCommand;
import com.winitech.smartAsset.domain.ticket.TicketCommentInfo;
import com.winitech.smartAsset.domain.ticket.TicketCompleteCommand;
import com.winitech.smartAsset.domain.ticket.TicketInfo;
import com.winitech.smartAsset.domain.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketFacade {

    private final TicketService ticketService;

    public UUID postTicketBySelf(TicketCommand command) {
        return ticketService.createTicketBySelf(command);
    }

    public UUID postTicketByAdmin(TicketCommand command, UUID requestedBy) {
        return ticketService.createTicketByAdmin(command, requestedBy);
    }

    public void reviseTicket(TicketCommand.UpdateCommand updateCommand) {
        ticketService.updateTicket(updateCommand);
    }

    public void assignTicket(UUID ticketId, UUID assigneeId) {
        ticketService.assignTicket(ticketId, assigneeId);
    }

    public void changeStatus(UUID ticketId, Ticket.Status newStatus) {
        ticketService.changeStatus(ticketId, newStatus);
    }

    public void completeTicket(UUID ticketId, TicketCompleteCommand completeCommand) {
        ticketService.completeTicket(ticketId, completeCommand);
    }

    public List<TicketInfo> getKanbanBoard() {
        return ticketService.loadKanbanBoard();
    }

    public Page<TicketInfo> getList(String keyword, Ticket.Status status, Integer page, Integer size) {
        return ticketService.loadList(keyword, status, page, size);
    }

    public TicketInfo getTicket(UUID ticketId) {
        return ticketService.loadTicket(ticketId);
    }

    public List<TicketInfo> getMyTickets() {
        return ticketService.loadMyTickets();
    }

    public List<TicketCommentInfo> getComments(UUID ticketId) {
        return ticketService.loadComments(ticketId);
    }

    public UUID addComment(UUID ticketId, String content) {
        return ticketService.addComment(ticketId, content);
    }
}
