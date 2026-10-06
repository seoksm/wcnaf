package com.winitech.system.application.programAction;

import com.winitech.common.domain.common.CommonMenuActionInfo;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.program.ProgramService;
import com.winitech.system.domain.programAction.ProgramActionCommand;
import com.winitech.system.domain.programAction.ProgramActionInfo;
import com.winitech.system.domain.programAction.ProgramActionService;
import com.winitech.system.interfaces.inboundAdapter.web.programAction.ProgramActionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramActionFacade {
	private final ProgramActionService programActionService;
	private final ProgramService programService;
	
	private final ProgramActionProducer programActionProducer;

	public ProgramActionInfo registerProgramAction(UUID programId, ProgramActionCommand.RegisterRequestCommand programActionCommand) {
		ProgramActionInfo result = programActionService.registerProgramAction(programId, programActionCommand);

		syncMenuAction(Arrays.asList(programId));

		return result;
	}

	public ProgramActionInfo modifyProgramAction(UUID programId, UUID id, ProgramActionCommand.ModifyRequestCommand programActionCommand) {
		ProgramActionInfo result = programActionService.modifyProgramAction(programId, id, programActionCommand);

		syncMenuAction(Arrays.asList(programId));

		return result;
	}

	public void removeProgramAction(UUID id) {
		ProgramActionInfo programActionInfo = programActionService.searchProgramActionById(id);
		
		programActionService.removeProgramAction(id);
		
		syncMenuAction(Arrays.asList(programActionInfo.getProgramId()));
	}

	public ProgramActionInfo searchProgramActionById(UUID id) {
		return programActionService.searchProgramActionById(id);
	}

	public List<ProgramActionInfo> getAllProgramAction() {
		return programActionService.getAllProgramAction();
	}

	public WiniPageInfo<ProgramActionInfo> searchProgramActionPage(UUID programId, Integer page, Integer pageSize, String searchType, String searchKeyword) {
		return programActionService.searchProgramActionPage(programId, page, pageSize, searchType, searchKeyword);
	}

	public List<ProgramActionInfo> registerProgramActionBatch(UUID programId, List<ProgramActionCommand.RegisterRequestCommand> commands) {
		List<ProgramActionInfo> result = programActionService.registerProgramActionBatch(programId, commands);
		
		syncMenuAction(Arrays.asList(programId));
		
		return result;
	}
	
	public void syncAllMenuAction() {
		List<UUID> programIdList = programService.getAllProgram().stream()
				.map(programInfo -> programInfo.getId())
				.collect(Collectors.toList());
		
		syncMenuAction(programIdList);
	}
	
	private void syncMenuAction(List<UUID> programIdList) {
		List<CommonMenuActionInfo> menuActionList = programActionService.searchMenuActionByProgramIdList(programIdList);
	
		programActionProducer.menuActionCdc(programIdList, menuActionList);
	}
}
