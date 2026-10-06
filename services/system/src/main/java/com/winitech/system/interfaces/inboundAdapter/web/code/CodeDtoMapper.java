package com.winitech.system.interfaces.inboundAdapter.web.code;

import com.winitech.system.domain.code.CodeCommand;
import com.winitech.system.domain.code.CodeInfo;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CodeDtoMapper {
	CodeDtoMapper INSTANCE = Mappers.getMapper(CodeDtoMapper.class);

	CodeResponseDto toCodeResponseDto(CodeInfo codeInfo);

	CodeCommand toRegisterRequestCommand(CodeRegisterRequestDto codeRegisterRequestDto);

	CodeCommand.UpdateCommand toModifyRequestCommand(CodeModifyRequestDto codeModifyRequestDto);;
}
