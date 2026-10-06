package com.winitech.smartAsset.interfaces.inboundAdapter.web.vendor;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.vendor.VendorFacade;
import com.winitech.smartAsset.domain.vendor.VendorCommand;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class VendorApiController implements VendorApi {

    private final VendorFacade vendorFacade;

    @Override
    public CommonResponse<VendorIdResponseDto> registerVendor(VendorRegisterRequestDto dto) {
        VendorCommand command = VendorDtoMapper.INSTANCE.toRegisterRequestCommand(dto);
        UUID id = vendorFacade.postVendor(command);
        return CommonResponse.success(VendorIdResponseDto.builder().vendorId(id).build());
    }

    @Override
    public CommonResponse<VendorIdResponseDto> modifyVendor(UUID vendorId, VendorModifyRequestDto dto) {
        VendorCommand.UpdateCommand updateCommand = VendorDtoMapper.INSTANCE.toModifyRequestCommand(dto);
        updateCommand.setVendorId(vendorId);
        vendorFacade.reviseVendor(updateCommand);
        return CommonResponse.success(VendorIdResponseDto.builder().vendorId(vendorId).build());
    }

    @Override
    public CommonResponse<String> removeVendor(UUID vendorId) {
        vendorFacade.removeVendor(vendorId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<VendorResponseDto>> searchAllVendor(String keyword) {
        List<VendorResponseDto> res = vendorFacade.getVendorList(keyword).stream()
                .map(VendorDtoMapper.INSTANCE::toVendorResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<VendorResponseDto> searchVendor(UUID vendorId) {
        return CommonResponse.success(VendorDtoMapper.INSTANCE.toVendorResponseDto(vendorFacade.getVendor(vendorId)));
    }
}
