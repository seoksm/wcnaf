package com.winitech.smartAsset.interfaces.inboundAdapter.web.ticket;

import com.winitech.smartAsset.domain.ticket.TicketCommand;
import com.winitech.smartAsset.domain.ticket.TicketCommentInfo;
import com.winitech.smartAsset.domain.ticket.TicketCompleteCommand;
import com.winitech.smartAsset.domain.ticket.TicketInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketDtoMapper {
    TicketDtoMapper INSTANCE = Mappers.getMapper(TicketDtoMapper.class);

    TicketResponseDto toTicketResponseDto(TicketInfo info);

    TicketCommand toRegisterRequestCommand(TicketRegisterRequestDto dto);

    TicketCommand toRegisterByAdminCommand(TicketRegisterByAdminRequestDto dto);

    TicketCommand.UpdateCommand toModifyRequestCommand(TicketModifyRequestDto dto);

    TicketCompleteCommand toCompleteCommand(TicketCompleteRequestDto dto);

    TicketCommentResponseDto toTicketCommentResponseDto(TicketCommentInfo info);

    default OffsetDateTime map(LocalDate value) {
        return value == null ? null : value.atTime(12, 0).atOffset(java.time.ZoneOffset.UTC);
    }

    default LocalDate map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDate();
    }
}
