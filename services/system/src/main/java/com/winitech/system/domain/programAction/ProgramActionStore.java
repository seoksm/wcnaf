package com.winitech.system.domain.programAction;

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
public interface ProgramActionStore {
	ProgramAction store(ProgramAction programAction);
	List<ProgramAction> storeAll(List<ProgramAction> toSaveList);
	ProgramAction modify(ProgramAction programAction, ProgramActionCommand.ModifyRequestCommand programActionCommand);
	void remove(UUID programActionId);
}
