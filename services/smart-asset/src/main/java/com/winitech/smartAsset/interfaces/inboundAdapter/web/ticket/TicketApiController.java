package com.winitech.smartAsset.interfaces.inboundAdapter.web.ticket;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.ticket.TicketFacade;
import com.winitech.smartAsset.domain.ticket.Ticket;
import com.winitech.smartAsset.domain.ticket.TicketCommand;
import com.winitech.smartAsset.domain.ticket.TicketCompleteCommand;
import com.winitech.smartAsset.domain.ticket.TicketInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class TicketApiController implements TicketApi {

    private final TicketFacade ticketFacade;

    @Override
    public CommonResponse<TicketIdResponseDto> registerTicketBySelf(TicketRegisterRequestDto dto) {
        TicketCommand command = TicketDtoMapper.INSTANCE.toRegisterRequestCommand(dto);
        UUID id = ticketFacade.postTicketBySelf(command);
        return CommonResponse.success(TicketIdResponseDto.builder().ticketId(id).build());
    }

    @Override
    public CommonResponse<TicketIdResponseDto> registerTicketByAdmin(TicketRegisterByAdminRequestDto dto) {
        TicketCommand command = TicketDtoMapper.INSTANCE.toRegisterByAdminCommand(dto);
        UUID id = ticketFacade.postTicketByAdmin(command, dto.getRequestedBy());
        return CommonResponse.success(TicketIdResponseDto.builder().ticketId(id).build());
    }

    @Override
    public CommonResponse<String> modifyTicket(UUID ticketId, TicketModifyRequestDto dto) {
        TicketCommand.UpdateCommand updateCommand = TicketDtoMapper.INSTANCE.toModifyRequestCommand(dto);
        updateCommand.setTicketId(ticketId);
        ticketFacade.reviseTicket(updateCommand);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> assignTicket(UUID ticketId, TicketAssignRequestDto dto) {
        ticketFacade.assignTicket(ticketId, dto.getAssigneeId());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> changeTicketStatus(UUID ticketId, TicketStatusChangeRequestDto dto) {
        ticketFacade.changeStatus(ticketId, Ticket.Status.valueOf(dto.getStatus().name()));
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> completeTicket(UUID ticketId, TicketCompleteRequestDto dto) {
        TicketCompleteCommand completeCommand = dto != null ? TicketDtoMapper.INSTANCE.toCompleteCommand(dto) : null;
        ticketFacade.completeTicket(ticketId, completeCommand);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<TicketPageResponseDto> searchAllTicket(String keyword, String status, Integer page, Integer size) {
        Ticket.Status statusFilter = status == null ? null : Ticket.Status.valueOf(status);
        Page<TicketInfo> ticketPage = ticketFacade.getList(keyword, statusFilter, page, size);

        List<TicketResponseDto> content = ticketPage.getContent().stream()
                .map(TicketDtoMapper.INSTANCE::toTicketResponseDto)
                .collect(Collectors.toList());

        TicketPageResponseDto res = TicketPageResponseDto.builder()
                .content(content)
                .currentPage(ticketPage.getNumber())
                .pageSize(ticketPage.getSize())
                .totalElements(ticketPage.getTotalElements())
                .totalPages(ticketPage.getTotalPages())
                .build();
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<TicketResponseDto> searchTicket(UUID ticketId) {
        return CommonResponse.success(TicketDtoMapper.INSTANCE.toTicketResponseDto(ticketFacade.getTicket(ticketId)));
    }

    @Override
    public CommonResponse<List<TicketResponseDto>> searchTicketKanbanBoard() {
        List<TicketResponseDto> res = ticketFacade.getKanbanBoard().stream()
                .map(TicketDtoMapper.INSTANCE::toTicketResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<List<TicketResponseDto>> searchMyTicket() {
        List<TicketResponseDto> res = ticketFacade.getMyTickets().stream()
                .map(TicketDtoMapper.INSTANCE::toTicketResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<List<TicketCommentResponseDto>> searchAllTicketComment(UUID ticketId) {
        List<TicketCommentResponseDto> res = ticketFacade.getComments(ticketId).stream()
                .map(TicketDtoMapper.INSTANCE::toTicketCommentResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<TicketCommentIdResponseDto> registerTicketComment(UUID ticketId, TicketCommentRegisterRequestDto dto) {
        UUID id = ticketFacade.addComment(ticketId, dto.getContent());
        return CommonResponse.success(TicketCommentIdResponseDto.builder().ticketCommentId(id).build());
    }
}
