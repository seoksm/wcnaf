package com.winitech.system.domain.programAction;

import com.winitech.common.domain.common.CommonMenuActionInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.programAction
 * └ ProgramAction.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-14 10:39
 **/
public interface ProgramActionReader {
	ProgramAction getProgramActionById(UUID programActionId);
	// ProgramAction getProgramActionByProgramActionCode(String programActionCode);
	List<ProgramAction> getAllProgramAction();
	List<ProgramAction> getProgramActionByProgramId(UUID programId);
	Page<ProgramActionInfo> getProgramActionPage(UUID programId, Integer page, Integer pageSize, String searchType, String searchKeyword);
	boolean existProgramActionAndExcludingSelf(UUID programId, ProgramAction.ActionType actionType, ProgramAction.AuthType authType, String uri, UUID programActionId);

	List<CommonMenuActionInfo> getMenuActionByProgramIdList(List<UUID> programIdList);
}
