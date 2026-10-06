package com.winitech.smartAsset.interfaces.inboundAdapter.web.processConfig;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.processConfig.ProcessConfigFacade;
import com.winitech.smartAsset.domain.processConfig.ProcessConfigCommand;
import com.winitech.smartAsset.domain.processConfig.ProcessConfigInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class ProcessConfigApiController implements ProcessConfigApi {

    private final ProcessConfigFacade processConfigFacade;

    @Override
    public CommonResponse<ProcessConfigResponseDto> searchProcessConfig() {
        ProcessConfigInfo config = processConfigFacade.getConfig();

        return CommonResponse.success(ProcessConfigDtoMapper.INSTANCE.toProcessConfigResponseDto(config));
    }

    @Override
    public CommonResponse<String> modifyProcessConfig(ProcessConfigModifyRequestDto processConfigModifyRequestDto) {
        ProcessConfigCommand.UpdateCommand updateCommand = ProcessConfigDtoMapper.INSTANCE.toModifyRequestCommand(processConfigModifyRequestDto);

        processConfigFacade.reviseConfig(updateCommand);
        return CommonResponse.success("OK");
    }
}
