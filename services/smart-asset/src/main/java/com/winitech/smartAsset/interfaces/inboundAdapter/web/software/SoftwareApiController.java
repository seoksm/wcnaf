package com.winitech.smartAsset.interfaces.inboundAdapter.web.software;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.software.SoftwareFacade;
import com.winitech.smartAsset.domain.software.SoftwareCommand;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class SoftwareApiController implements SoftwareApi {

    private final SoftwareFacade softwareFacade;

    @Override
    public CommonResponse<SoftwareIdResponseDto> registerSoftware(SoftwareRegisterRequestDto dto) {
        SoftwareCommand command = SoftwareDtoMapper.INSTANCE.toRegisterRequestCommand(dto);
        UUID id = softwareFacade.postSoftware(command);
        return CommonResponse.success(SoftwareIdResponseDto.builder().softwareId(id).build());
    }

    @Override
    public CommonResponse<SoftwareIdResponseDto> modifySoftware(UUID softwareId, SoftwareModifyRequestDto dto) {
        SoftwareCommand.UpdateCommand updateCommand = SoftwareDtoMapper.INSTANCE.toModifyRequestCommand(dto);
        updateCommand.setSoftwareId(softwareId);
        softwareFacade.reviseSoftware(updateCommand);
        return CommonResponse.success(SoftwareIdResponseDto.builder().softwareId(softwareId).build());
    }

    @Override
    public CommonResponse<String> removeSoftware(UUID softwareId) {
        softwareFacade.removeSoftware(softwareId);
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<SoftwareResponseDto>> searchAllSoftware(String keyword) {
        List<SoftwareResponseDto> res = softwareFacade.getSoftwareList(keyword).stream()
                .map(SoftwareDtoMapper.INSTANCE::toSoftwareResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse<SoftwareResponseDto> searchSoftware(UUID softwareId) {
        return CommonResponse.success(SoftwareDtoMapper.INSTANCE.toSoftwareResponseDto(softwareFacade.getSoftware(softwareId)));
    }
}
