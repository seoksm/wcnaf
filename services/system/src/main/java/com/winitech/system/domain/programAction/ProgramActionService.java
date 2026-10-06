package com.winitech.system.domain.programAction;

import com.winitech.common.domain.common.CommonMenuActionInfo;
import com.winitech.common.library.commonType.WiniPageInfo;

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
public interface ProgramActionService {
	ProgramActionInfo registerProgramAction(UUID programId, ProgramActionCommand.RegisterRequestCommand programActionCommand);
	List<ProgramActionInfo> registerProgramActionBatch(UUID programId, List<ProgramActionCommand.RegisterRequestCommand> commands);
	ProgramActionInfo modifyProgramAction(UUID programId, UUID id, ProgramActionCommand.ModifyRequestCommand programActionCommand);
	void removeProgramAction(UUID id);
	ProgramActionInfo searchProgramActionById(UUID id);
	List<ProgramActionInfo> getAllProgramAction();
	WiniPageInfo<ProgramActionInfo> searchProgramActionPage(UUID programId, Integer page, Integer pageSize, String searchType, String searchKeyword);

	List<CommonMenuActionInfo> searchMenuActionByProgramIdList(List<UUID> programIdList);
}
