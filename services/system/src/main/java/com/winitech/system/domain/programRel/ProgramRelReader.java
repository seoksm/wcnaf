package com.winitech.system.domain.programRel;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.system.domain.program.Program;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.programRel
 * └ ProgramRelReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 09:05
 **/
public interface ProgramRelReader {
	ProgramRel getProgramRelById(UUID id);

	List<ProgramRel> getAllProgramRelByProgramId(UUID programId);

	List<ProgramRel> getAllProgramRelByProgramIdList(List<UUID> programIdList);
	
	List<Program> getAllRelatedProgramRelByProgramId(UUID programId);

	List<ProgramRel> getAllProgramRelByRelProgramId(UUID programId);
}