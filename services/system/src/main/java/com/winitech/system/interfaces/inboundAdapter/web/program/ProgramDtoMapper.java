package com.winitech.system.interfaces.inboundAdapter.web.program;

import com.winitech.system.domain.program.ProgramCommand;
import com.winitech.system.domain.program.ProgramInfo;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

/**
 * @author 강동구
 * @version 1.0
 * @see <pre>
 *  Modification Information
 *
 * 	수정일     / 수정자   / 수정내용
 * 	------------------------------------------
 * 	2025-01-22 / 강동구  / 최초 생성
 * </pre>
 * @since 2025-01-22
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProgramDtoMapper {
	ProgramDtoMapper INSTANCE = Mappers.getMapper(ProgramDtoMapper.class);
	
	@Mapping(target = "programId", source = "id")
	ProgramResponseDto toProgramResponseDto(ProgramInfo programInfo);
	
	@Mapping(target = "programId", source = "id")
	@Mapping(target = "relProgramList", source = "relProgramList")
	ProgramDetailResponseDto toProgramDetailResponseDto(ProgramInfo.DetailInfo programInfo);

	@Mapping(target = "programId", source = "id")
	ProgramResponseRelProgramDto toProgramResponseRelProgramDto(ProgramInfo programInfo);

	@Mapping(target = "relProgramIdList", source = "relProgramIdList")
	ProgramCommand.RegisterRequestCommand toRegisterRequestCommand(ProgramRegisterRequestDto programRegisterRequestDto);

	@Mapping(target = "relProgramIdList", source = "relProgramIdList")
	ProgramCommand.ModifyRequestCommand toModifyRequestCommand(ProgramModifyRequestDto programModifyRequestDto);

	@Mapping(target = "programId", source = "id")
	ProgramStoreResponseDto toProgramStoreResponseDto(ProgramInfo programInfo);
}
