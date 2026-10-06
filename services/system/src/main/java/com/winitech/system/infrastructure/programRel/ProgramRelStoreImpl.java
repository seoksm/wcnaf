package com.winitech.system.infrastructure.programRel;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.programRel.ProgramRel;
import com.winitech.system.domain.programRel.ProgramRelCommand;
import com.winitech.system.domain.programRel.ProgramRelReader;
import com.winitech.system.domain.programRel.ProgramRelStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.programRel
 * └ ProgramRelStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 09:06
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramRelStoreImpl implements ProgramRelStore {
	private final ProgramRelRepository programRelRepository;
	private final ProgramRelReader programRelReader;

	@Override
	public ProgramRel store(ProgramRel programRel) {
		return programRelRepository.save(programRel);
	}

	@Override
	public List<ProgramRel> storeList(List<ProgramRel> programRelList) {
		return programRelRepository.saveAll(programRelList);
	}

	@Override
	public ProgramRel modify(ProgramRel programRel, ProgramRelCommand command) {
		programRel.setProgram(Program.builder().id(command.getProgramId()).build());
		programRel.setRelProgram(Program.builder().id(command.getRelProgramId()).build());
		return programRelRepository.save(programRel);
	}
	
	@Override
	public void delete(UUID id) {
		programRelRepository.deleteById(id);
	}

	@Override
	public void deleteList(List<UUID> ids) {
		programRelRepository.deleteAllById(ids);
	}

	@Override
	public void removeByProgramId(UUID programId) {
		List<ProgramRel> programRelList = programRelReader.getAllProgramRelByProgramId(programId);
		programRelRepository.deleteAll(programRelList);
	}

	@Override
	public void removeByRelProgramId(UUID relProgramId) {
		List<ProgramRel> programRelList = programRelReader.getAllProgramRelByRelProgramId(relProgramId);
		programRelRepository.deleteAll(programRelList);
	}
}
