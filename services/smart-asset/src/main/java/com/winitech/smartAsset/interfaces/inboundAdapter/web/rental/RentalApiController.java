package com.winitech.smartAsset.interfaces.inboundAdapter.web.rental;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.rental.RentalFacade;
import com.winitech.smartAsset.domain.rental.RentalCommand;
import com.winitech.smartAsset.domain.rental.RentalInfo;
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
public class RentalApiController implements RentalApi {

    private final RentalFacade rentalFacade;

    @Override
    public CommonResponse<RentalIdResponseDto> registerRental(RentalRegisterRequestDto dto) {
        RentalCommand command = RentalDtoMapper.INSTANCE.toRegisterRequestCommand(dto);
        UUID id = rentalFacade.postRental(command);
        return CommonResponse.success(RentalIdResponseDto.builder().rentalAssetId(id).build());
    }

    @Override
    public CommonResponse<RentalIdResponseDto> modifyRental(UUID rentalAssetId, RentalModifyRequestDto dto) {
        RentalCommand.UpdateCommand updateCommand = RentalDtoMapper.INSTANCE.toModifyRequestCommand(dto);
        updateCommand.setRentalAssetId(rentalAssetId);
        rentalFacade.reviseRental(updateCommand);
        return CommonResponse.success(RentalIdResponseDto.builder().rentalAssetId(rentalAssetId).build());
    }

    @Override
    public CommonResponse<String> cancelRental(UUID rentalAssetId) {
        rentalFacade.cancelRental(rentalAssetId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<RentalPageResponseDto> searchAllRental(String keyword, Integer page, Integer size) {
        Page<RentalInfo> rentalPage = rentalFacade.getList(keyword, page, size);

        List<RentalResponseDto> content = rentalPage.getContent().stream()
                .map(RentalDtoMapper.INSTANCE::toRentalResponseDto)
                .collect(Collectors.toList());

        RentalPageResponseDto res = RentalPageResponseDto.builder()
                .content(content)
                .currentPage(rentalPage.getNumber())
                .pageSize(rentalPage.getSize())
                .totalElements(rentalPage.getTotalElements())
                .totalPages(rentalPage.getTotalPages())
                .build();
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<RentalResponseDto> searchRental(UUID rentalAssetId) {
        return CommonResponse.success(RentalDtoMapper.INSTANCE.toRentalResponseDto(rentalFacade.getRental(rentalAssetId)));
    }

    @Override
    public CommonResponse<List<PaymentScheduleResponseDto>> searchAllPaymentSchedule(UUID rentalAssetId) {
        List<PaymentScheduleResponseDto> res = rentalFacade.getPaymentSchedules(rentalAssetId).stream()
                .map(RentalDtoMapper.INSTANCE::toPaymentScheduleResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<PaymentScheduleIdResponseDto> registerPaymentSchedule(UUID rentalAssetId, PaymentScheduleRegisterRequestDto dto) {
        UUID id = rentalFacade.addPaymentSchedule(
                rentalAssetId,
                dto.getAccrualMonth().toLocalDate(),
                dto.getDueDate() != null ? dto.getDueDate().toLocalDate() : null,
                dto.getExpectedAmount());
        return CommonResponse.success(PaymentScheduleIdResponseDto.builder().paymentScheduleId(id).build());
    }

    @Override
    public CommonResponse<String> modifyPaymentSchedule(UUID paymentScheduleId, PaymentScheduleModifyRequestDto dto) {
        rentalFacade.updatePaymentSchedule(
                paymentScheduleId,
                dto.getDueDate() != null ? dto.getDueDate().toLocalDate() : null,
                dto.getExpectedAmount());
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<String> confirmPaymentSchedule(UUID paymentScheduleId, PaymentScheduleConfirmRequestDto dto) {
        rentalFacade.confirmPaymentSchedule(paymentScheduleId, dto.getActualAmount());
        return CommonResponse.success("OK");
    }
}
