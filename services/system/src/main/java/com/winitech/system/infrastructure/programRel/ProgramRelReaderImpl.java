package com.winitech.system.infrastructure.programRel;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.programRel.ProgramRel;
import com.winitech.system.domain.programRel.ProgramRelReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.programRel
 * └ ProgramRelReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 09:06
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramRelReaderImpl implements ProgramRelReader {
	private final ProgramRelRepository programRelRepository;

	@Override
	public ProgramRel getProgramRelById(UUID id) {
		return programRelRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<ProgramRel> getAllProgramRelByProgramId(UUID programId) {
		return programRelRepository.findAllByProgramId(programId);
	}

	@Override
	public List<ProgramRel> getAllProgramRelByProgramIdList(List<UUID> programIdList) {
		return programRelRepository.findAllByProgramIdIn(programIdList);
	}

	@Override
	public List<Program> getAllRelatedProgramRelByProgramId(UUID programId) {
		return programRelRepository.findAllRelProgramByProgramId(programId);
	}

	@Override
	public List<ProgramRel> getAllProgramRelByRelProgramId(UUID programId) {
		return programRelRepository.findAllByRelProgramId(programId);		
	}
}
