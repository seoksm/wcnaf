package com.winitech.system.interfaces.inboundAdapter.web.program;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.response.CommonResponse;
import com.winitech.system.application.program.ProgramFacade;
import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.program.ProgramCommand;
import com.winitech.system.domain.program.ProgramInfo;
import com.winitech.system.domain.programRel.ProgramRelReader;
import com.winitech.system.domain.programRel.ProgramRelStore;
import com.winitech.system.infrastructure.programRel.ProgramRelRepository;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.program
 * └ ProgramApiController.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-22 17:43
 **/
@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class ProgramApiController implements ProgramApi {
	private final ProgramFacade programFacade;

	@Override
	public CommonResponse<List<ProgramResponseDto>> searchAllProgram(Integer page, Integer pageSize, String searchType, String searchKeyword, String menuStatus) {
		Program.MenuStatus enumMenuStatus = null;
		if (menuStatus != null && !menuStatus.isEmpty()) {
			enumMenuStatus = Program.MenuStatus.valueOf(menuStatus);
		}
		
		WiniPageInfo<ProgramInfo> programPageInfo = programFacade.getAllProgram(page, pageSize, searchType, searchKeyword, enumMenuStatus);

		List<ProgramResponseDto> response = programPageInfo
				.stream()
				.map(ProgramDtoMapper.INSTANCE::toProgramResponseDto)
				.collect(Collectors.toList());
		
		return CommonResponse.success(response, programPageInfo);
	}

	@Override
	public CommonResponse<ProgramDetailResponseDto> searchProgram(UUID programId) {
		ProgramInfo.DetailInfo programInfo = programFacade.searchProgram(programId);
		
		ProgramDetailResponseDto response = ProgramDtoMapper.INSTANCE.toProgramDetailResponseDto(programInfo);
		
		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<ProgramStoreResponseDto> registerProgram(ProgramRegisterRequestDto programRegisterRequestDto) {
		ProgramCommand.RegisterRequestCommand command 
				= ProgramDtoMapper.INSTANCE.toRegisterRequestCommand(programRegisterRequestDto);

		ProgramInfo programInfo = programFacade.registerProgram(command);

		ProgramStoreResponseDto response = ProgramDtoMapper.INSTANCE.toProgramStoreResponseDto(programInfo);
		
		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<ProgramStoreResponseDto> modifyProgram(UUID programId, ProgramModifyRequestDto programModifyRequestDto) {
		ProgramCommand.ModifyRequestCommand command
				= ProgramDtoMapper.INSTANCE.toModifyRequestCommand(programModifyRequestDto);

		ProgramInfo programInfo = programFacade.modifyProgram(programId, command);

		ProgramStoreResponseDto response = ProgramDtoMapper.INSTANCE.toProgramStoreResponseDto(programInfo);

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<String> removeProgram(UUID programId) {
		programFacade.removeProgram(programId);
		
		return CommonResponse.success(null);
	}
}
