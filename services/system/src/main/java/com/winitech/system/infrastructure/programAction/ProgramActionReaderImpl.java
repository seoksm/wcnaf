package com.winitech.system.infrastructure.programAction;

import com.winitech.common.domain.common.CommonMenuActionInfo;
import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import com.winitech.system.domain.programAction.ProgramAction;
import com.winitech.system.domain.programAction.ProgramActionInfo;
import com.winitech.system.domain.programAction.ProgramActionReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
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
public class ProgramActionReaderImpl implements ProgramActionReader {
	private final ProgramActionRepository programActionRepository;
	private final ProgramActionQueryRepository programActionQueryRepository;

	@Override
	public ProgramAction getProgramActionById(UUID programActionId) {
		return programActionRepository.findById(programActionId).orElseThrow(EntityNotFoundException::new);
	}

	//@Override
	//public ProgramAction getProgramActionByProgramActionCode(String programActionCode) {
	//	return programActionRepository.findByProgramActionCodeAndSystemStatus(programActionCode, ProgramAction.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	//}

	@Override
	public List<ProgramAction> getAllProgramAction() {
		return programActionRepository.findAllByOrderByIdDesc();
	}

	@Override
	public List<ProgramAction> getProgramActionByProgramId(UUID programId) {
		return programActionRepository.findAllByProgramId(programId);
	}

	@Override
	public Page<ProgramActionInfo> getProgramActionPage(UUID programId, Integer page, Integer pageSize, String searchType, String searchKeyword) {
		return programActionQueryRepository.findAllPage(programId, searchType, searchKeyword, WiniCom.getPageRequest(page, pageSize));
	}

	@Override
	public boolean existProgramActionAndExcludingSelf(UUID programId, ProgramAction.ActionType actionType, ProgramAction.AuthType authType, String uri, UUID programActionId) {
		return programActionRepository.existsByProgramIdAndActionTypeAndAuthTypeAndUriAndIdNot(programId, actionType, authType, uri, programActionId);
	}

	@Override
	public List<CommonMenuActionInfo> getMenuActionByProgramIdList(List<UUID> programIdList) {
		return programActionRepository.getMenuActionByProgramIdList(programIdList);
	}
}
