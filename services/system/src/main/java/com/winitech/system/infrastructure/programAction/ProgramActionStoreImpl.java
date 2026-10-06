package com.winitech.system.infrastructure.programAction;

import com.winitech.system.domain.programAction.ProgramAction;
import com.winitech.system.domain.programAction.ProgramActionCommand;
import com.winitech.system.domain.programAction.ProgramActionReader;
import com.winitech.system.domain.programAction.ProgramActionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.programAction
 * └ ProgramAction.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-14 10:39
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramActionStoreImpl implements ProgramActionStore {
	private final ProgramActionRepository programActionRepository;
	private final ProgramActionReader programActionReader;

	@Override
	public ProgramAction store(ProgramAction programAction) {
		return programActionRepository.save(programAction);
	}

	@Override
	public List<ProgramAction> storeAll(List<ProgramAction> toSaveList) {
		return programActionRepository.saveAll(toSaveList);
	}

	@Override
	public ProgramAction modify(ProgramAction programAction, ProgramActionCommand.ModifyRequestCommand command) {
	programAction.setActionType(command.getActionType());

	programAction.setAuthType(command.getAuthType());

	programAction.setUri(command.getUri());

		return programActionRepository.save(programAction);
	}

	@Override
	public void remove(UUID programActionId) {
		programActionRepository.deleteById(programActionId);
	}
}
