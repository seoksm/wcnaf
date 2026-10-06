package com.winitech.system.interfaces.inboundAdapter.web.programAction;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.response.CommonResponse;
import com.winitech.system.application.programAction.ProgramActionFacade;
import com.winitech.system.domain.programAction.ProgramActionCommand;
import com.winitech.system.domain.programAction.ProgramActionInfo;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.programAction
 * └ ProgramAction.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-14 10:39
 **/
@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class ProgramActionApiController implements ProgramActionApi {
	private final ProgramActionFacade programActionFacade;

	@Override
	public CommonResponse<List<ProgramActionResponseDto>> searchAllProgramAction(UUID programId, Integer page, Integer pageSize, String searchType, String searchKeyword) {
		WiniPageInfo<ProgramActionInfo> programActionPageInfo = programActionFacade.searchProgramActionPage(programId, page, pageSize, searchType, searchKeyword);
		
		List<ProgramActionResponseDto> response = programActionPageInfo
				.stream()
				.map(ProgramActionDtoMapper.INSTANCE::toProgramActionResponseDto)
				.collect(Collectors.toList());

		return CommonResponse.success(response, programActionPageInfo);
	}

	@Override
	public CommonResponse<ProgramActionDetailResponseDto> searchProgramAction(UUID programId, UUID actionId) {
		ProgramActionInfo programActionInfo = programActionFacade.searchProgramActionById(actionId);
		
		ProgramActionDetailResponseDto response = ProgramActionDtoMapper.INSTANCE.toProgramActionDetailResponseDto(programActionInfo);

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<ProgramActionStoreResponseDto> registerProgramAction(UUID programId, ProgramActionRegisterRequestDto programActionRegisterRequestDto) {
		ProgramActionCommand.RegisterRequestCommand command = ProgramActionDtoMapper.INSTANCE.toRegisterRequestCommand(programActionRegisterRequestDto);

		ProgramActionInfo programActionInfo = programActionFacade.registerProgramAction(programId, command);

		ProgramActionStoreResponseDto response = ProgramActionDtoMapper.INSTANCE.toProgramActionStoreResponseDto(programActionInfo);

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<ProgramActionBatchStoreResponseDto> registerProgramActionBatch(UUID programId, ProgramActionBatchRegisterRequestDto programActionBatchRegisterRequestDto) {
		List<ProgramActionCommand.RegisterRequestCommand> commands = programActionBatchRegisterRequestDto.getActionList()
				.stream()
				.map(ProgramActionDtoMapper.INSTANCE::toRegisterRequestCommand)
				.collect(Collectors.toList());

		List<ProgramActionInfo> programActionInfos = programActionFacade.registerProgramActionBatch(programId, commands);

		ProgramActionBatchStoreResponseDto response = new ProgramActionBatchStoreResponseDto();
		response.setProgramActionIdList(programActionInfos.stream()
				.map(ProgramActionDtoMapper.INSTANCE::toProgramActionStoreResponseDto)
				.collect(Collectors.toList()));

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<ProgramActionStoreResponseDto> modifyProgramAction(UUID programId, UUID actionId, ProgramActionModifyRequestDto programActionModifyRequestDto) {
		ProgramActionCommand.ModifyRequestCommand command = ProgramActionDtoMapper.INSTANCE.toModifyRequestCommand(programActionModifyRequestDto);

		ProgramActionInfo programActionInfo = programActionFacade.modifyProgramAction(programId, actionId, command);

		ProgramActionStoreResponseDto response = ProgramActionDtoMapper.INSTANCE.toProgramActionStoreResponseDto(programActionInfo);

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<String> removeProgramAction(UUID programId, UUID actionId) {
		programActionFacade.removeProgramAction(actionId);

		return CommonResponse.success("OK");
	}

	@Override
	public CommonResponse<String> syncAllProgramAction() {
		programActionFacade.syncAllMenuAction();
		
		return CommonResponse.success("OK");
	}
}
