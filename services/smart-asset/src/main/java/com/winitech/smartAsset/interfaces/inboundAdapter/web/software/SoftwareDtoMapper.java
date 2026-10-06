package com.winitech.smartAsset.interfaces.inboundAdapter.web.software;

import com.winitech.smartAsset.domain.software.SoftwareCommand;
import com.winitech.smartAsset.domain.software.SoftwareInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SoftwareDtoMapper {
    SoftwareDtoMapper INSTANCE = Mappers.getMapper(SoftwareDtoMapper.class);

    SoftwareResponseDto toSoftwareResponseDto(SoftwareInfo info);

    SoftwareCommand toRegisterRequestCommand(SoftwareRegisterRequestDto dto);

    SoftwareCommand.UpdateCommand toModifyRequestCommand(SoftwareModifyRequestDto dto);
}
