package com.winitech.system.domain.programRel;

import com.winitech.system.domain.program.ProgramInfo;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.programRel
 * └ ProgramRelService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 10:34
 **/
public interface ProgramRelService {
	List<ProgramRelInfo> saveProgramRelList(UUID programId, List<UUID> relProgramIdList);
	
	List<ProgramRelInfo> getProgramRelList(UUID programId);
	
	List<ProgramRelInfo> getProgramRelList(List<UUID> programIdList);

	List<ProgramInfo> getRelProgramList(UUID programId);

	void removeByProgramId(UUID programId);
	
	void removeByRelProgramId(UUID relProgramId);
}
