package com.winitech.system.interfaces.inboundAdapter.web.programAction;

import com.winitech.system.domain.programAction.ProgramActionCommand;
import com.winitech.system.domain.programAction.ProgramActionInfo;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.programAction
 * └ ProgramActionDtoMapper.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-14 10:50
 **/
@Mapper
public interface ProgramActionDtoMapper {
	ProgramActionDtoMapper INSTANCE = Mappers.getMapper(ProgramActionDtoMapper.class);
	
	@Mapping(source = "id", target = "programActionId")
	ProgramActionResponseDto toProgramActionResponseDto(ProgramActionInfo programActionInfo);

	@Mapping(source = "id", target = "programActionId")
	ProgramActionDetailResponseDto toProgramActionDetailResponseDto(ProgramActionInfo programActionInfo);

	ProgramActionCommand.RegisterRequestCommand toRegisterRequestCommand(ProgramActionRegisterRequestDto programActionRegisterRequestDto);
	
	ProgramActionCommand.ModifyRequestCommand toModifyRequestCommand(ProgramActionModifyRequestDto programActionModifyRequestDto);

	@Mapping(source = "id", target = "programActionId")
	ProgramActionStoreResponseDto toProgramActionStoreResponseDto(ProgramActionInfo programActionInfo);
}
