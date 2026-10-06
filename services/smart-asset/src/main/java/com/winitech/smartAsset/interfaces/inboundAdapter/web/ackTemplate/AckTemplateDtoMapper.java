package com.winitech.smartAsset.interfaces.inboundAdapter.web.ackTemplate;

import com.winitech.smartAsset.domain.ackTemplate.AckTemplateInfo;
import com.winitech.smartAsset.interfaces.inboundAdapter.spec.AckTemplateResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AckTemplateDtoMapper {
    AckTemplateDtoMapper INSTANCE = Mappers.getMapper(AckTemplateDtoMapper.class);

    AckTemplateResponseDto toAckTemplateResponseDto(AckTemplateInfo ackTemplateInfo);
}
