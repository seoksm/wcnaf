package com.winitech.system.domain.programRel;

import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.program.ProgramInfo;
import com.winitech.system.domain.program.ProgramReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.programRel
 * └ ProgramRelServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 10:35
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramRelServiceImpl extends EgovAbstractServiceImpl implements ProgramRelService {
	private final ProgramRelStore programRelStore;
	private final ProgramRelReader programRelReader;
	
	private final ProgramReader programReader;

	@Override
	public List<ProgramRelInfo> saveProgramRelList(UUID programId, List<UUID> relProgramIdList) {
		Program program = programReader.getProgramById(programId);

		Map<UUID, ProgramRel> relProgramIdToProgramRelMap = new HashMap<>();
		List<ProgramRel> programRelList = programRelReader.getAllProgramRelByProgramId(programId);

		programRelList.forEach(it -> {
			relProgramIdToProgramRelMap.put(it.getRelProgram().getId(), it);
		});

		List<ProgramRel> toSaveList = new ArrayList<>();

		if (relProgramIdList != null) {
			relProgramIdList.forEach(relProgramId -> {
				if (relProgramIdToProgramRelMap.containsKey(relProgramId)) {
					// 기존에 관계 프로그램이 존재하는 경우 추가하지 않음
					
					relProgramIdToProgramRelMap.remove(relProgramId);
				} else {
					// 관계 프로그램이 존재하지 않는 경우 추가
					
					ProgramRel programRel = ProgramRel
							.builder()
							.program(program)
							.relProgram(programReader.getProgramById(relProgramId))
							.build();

					toSaveList.add(programRel);
				}
			});

			if (toSaveList.size() > 0) {
				programRelStore.storeList(toSaveList);
			}
		}

		if (! relProgramIdToProgramRelMap.isEmpty()) {
			List<UUID> deleteIdList = relProgramIdToProgramRelMap.values()
					.stream()
					.map(ProgramRel::getId)
					.collect(Collectors.toList());

			programRelStore.deleteList(deleteIdList);
		}
		
		return toSaveList
				.stream()
				.map(ProgramRelInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<ProgramRelInfo> getProgramRelList(UUID programId) {
		List<ProgramRel> programRelList = programRelReader.getAllProgramRelByProgramId(programId);
		
		return programRelList
				.stream()
				.map(ProgramRelInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<ProgramRelInfo> getProgramRelList(List<UUID> programIdList) {
		List<ProgramRel> programRelList = programRelReader.getAllProgramRelByProgramIdList(programIdList);
		return programRelList
				.stream()
				.map(ProgramRelInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<ProgramInfo> getRelProgramList(UUID programId) {
		List<Program> relProgramList = programRelReader.getAllRelatedProgramRelByProgramId(programId);
		
		return relProgramList
				.stream()
				.map(ProgramInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public void removeByProgramId(UUID programId) {
		programRelStore.removeByProgramId(programId);
	}
	
	@Override
	public void removeByRelProgramId(UUID relProgramId) {
		programRelStore.removeByRelProgramId(relProgramId);
	}
}
