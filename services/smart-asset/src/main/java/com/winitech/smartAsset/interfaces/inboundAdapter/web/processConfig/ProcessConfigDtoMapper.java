package com.winitech.smartAsset.interfaces.inboundAdapter.web.processConfig;

import com.winitech.smartAsset.domain.processConfig.ProcessConfigCommand;
import com.winitech.smartAsset.domain.processConfig.ProcessConfigInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProcessConfigDtoMapper {
    ProcessConfigDtoMapper INSTANCE = Mappers.getMapper(ProcessConfigDtoMapper.class);

    ProcessConfigResponseDto toProcessConfigResponseDto(ProcessConfigInfo processConfigInfo);

    ProcessConfigCommand.UpdateCommand toModifyRequestCommand(ProcessConfigModifyRequestDto processConfigModifyRequestDto);
}
