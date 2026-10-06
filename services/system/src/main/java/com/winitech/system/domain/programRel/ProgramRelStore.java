package com.winitech.system.domain.programRel;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.programRel
 * └ ProgramRelStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 09:05
 **/
public interface ProgramRelStore {
	ProgramRel store(ProgramRel programRel);

	List<ProgramRel> storeList(List<ProgramRel> programRelList);
	
	ProgramRel modify(ProgramRel programRel, ProgramRelCommand command);

    void delete(UUID id);
	
	void deleteList(List<UUID> ids);

	void removeByProgramId(UUID programId);
	
	void removeByRelProgramId(UUID relProgramId);
}
