package com.winitech.system.domain.programAction;

import com.winitech.common.domain.common.CommonMenuActionInfo;
import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.program.Program;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
@Transactional
public class ProgramActionServiceImpl extends EgovAbstractServiceImpl implements ProgramActionService {
	private final ProgramActionStore programActionStore;
	private final ProgramActionReader programActionReader;

	@Override
	public ProgramActionInfo registerProgramAction(UUID programId, ProgramActionCommand.RegisterRequestCommand programActionCommand) {
		boolean isExisting = programActionReader.existProgramActionAndExcludingSelf(
				programId,
				programActionCommand.getActionType(),
				programActionCommand.getAuthType(),
				programActionCommand.getUri(), 
				UUID.randomUUID()
		);
		
		if (isExisting) {
			throw new IllegalStatusException("Already registered program action");
		}

		ProgramAction initProgramAction = ProgramAction.builder()
				.program(Program.builder().id(programId).build())
				.actionType(programActionCommand.getActionType())
				.authType(programActionCommand.getAuthType())
				.uri(programActionCommand.getUri())
				.build();

		ProgramAction programAction = programActionStore.store(initProgramAction);
		return new ProgramActionInfo(programAction);
	}

	@Override
	public List<ProgramActionInfo> registerProgramActionBatch(UUID programId, List<ProgramActionCommand.RegisterRequestCommand> commands) {
		Program program = Program.builder().id(programId).build();
		
		List<ProgramAction> programActionList = commands.stream()
				.map(command -> ProgramAction.builder()
						.program(program)
						.actionType(command.getActionType())
						.authType(command.getAuthType())
						.uri(command.getUri())
						.build())
				.collect(Collectors.toList());

		final Map<String, ProgramAction> prevProgramActionMap = new HashMap<>();
		final Map<String, ProgramAction> toDeleteMap = prevProgramActionMap;
		
		// 기존 항목 조회하여 Map에 저장
		programActionReader.getProgramActionByProgramId(programId).forEach(programAction -> {
			String key = programAction.getActionType().toString() + "_" + programAction.getAuthType().toString() + "_" + programAction.getUri();
			prevProgramActionMap.put(key, programAction);
		});
		
		List<ProgramAction> toSaveList = new ArrayList<>();
		Set<String> toSaveKeySet = new HashSet<>();
		
		// 신규 항목 및 수정된 항목을 toSaveList에 저장 
		programActionList.forEach(programAction -> {
			String key = programAction.getActionType().toString() + "_" + programAction.getAuthType().toString() + "_" + programAction.getUri();
			
			if (toSaveKeySet.contains(key)) {
				return;
			}
			
			if (prevProgramActionMap.containsKey(key)) {
				ProgramAction prevProgramAction = prevProgramActionMap.get(key);
				toSaveList.add(prevProgramAction);
				
				// 기존에 있는 항목 중 사용되는 항목은 삭제 대상에서 제외 
				toDeleteMap.remove(key);
			} else {
				toSaveList.add(programAction);
			}

			toSaveKeySet.add(key);
		});

		// 신규 항목 및 수정된 항목 저장
		List<ProgramAction> storedProgramActionList = programActionStore.storeAll(toSaveList);

		// 기존 항목중 삭제된 항목 삭제
		toDeleteMap.values().forEach(programAction -> programActionStore.remove(programAction.getId()));
		
		return storedProgramActionList.stream()
				.map(ProgramActionInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public ProgramActionInfo modifyProgramAction(UUID programId, UUID id, ProgramActionCommand.ModifyRequestCommand programActionCommand) {
		boolean isExisting = programActionReader.existProgramActionAndExcludingSelf(
				programId, 
				programActionCommand.getActionType(),
				programActionCommand.getAuthType(),
				programActionCommand.getUri(),
				id
		);

		if (isExisting) {
			throw new IllegalStatusException("Already registered program action");
		}

		ProgramAction modifyProgramAction = programActionReader.getProgramActionById(id);

		ProgramAction programAction = programActionStore.modify(modifyProgramAction, programActionCommand);
		return new ProgramActionInfo(programAction);
	}

	@Override
	public void removeProgramAction(UUID id) {
		programActionStore.remove(id);
	}

	@Override
	public ProgramActionInfo searchProgramActionById(UUID id) {
		ProgramAction programAction = programActionReader.getProgramActionById(id);
		return new ProgramActionInfo(programAction);
	}

	@Override
	public List<ProgramActionInfo> getAllProgramAction() {
		List<ProgramAction> programActionList = programActionReader.getAllProgramAction();
		return programActionList.stream()
				.map(ProgramActionInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public WiniPageInfo<ProgramActionInfo> searchProgramActionPage(UUID programId, Integer page, Integer pageSize, String searchType, String searchKeyword) {
		Page<ProgramActionInfo> programActionPage = programActionReader.getProgramActionPage(programId, page, pageSize, searchType, searchKeyword);

		return new WiniPageInfo<>(programActionPage);
	}

	@Override
	public List<CommonMenuActionInfo> searchMenuActionByProgramIdList(List<UUID> programIdList) {
		return programActionReader.getMenuActionByProgramIdList(programIdList);		
	}
}
