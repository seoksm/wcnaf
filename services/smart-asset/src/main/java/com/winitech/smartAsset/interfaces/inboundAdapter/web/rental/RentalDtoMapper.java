package com.winitech.smartAsset.interfaces.inboundAdapter.web.rental;

import com.winitech.smartAsset.domain.rental.PaymentScheduleInfo;
import com.winitech.smartAsset.domain.rental.RentalCommand;
import com.winitech.smartAsset.domain.rental.RentalInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RentalDtoMapper {
    RentalDtoMapper INSTANCE = Mappers.getMapper(RentalDtoMapper.class);

    RentalResponseDto toRentalResponseDto(RentalInfo info);

    RentalCommand toRegisterRequestCommand(RentalRegisterRequestDto dto);

    RentalCommand.UpdateCommand toModifyRequestCommand(RentalModifyRequestDto dto);

    PaymentScheduleResponseDto toPaymentScheduleResponseDto(PaymentScheduleInfo info);

    default OffsetDateTime map(LocalDate value) {
        return value == null ? null : value.atTime(12, 0).atOffset(java.time.ZoneOffset.UTC);
    }

    default LocalDate map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDate();
    }
}
