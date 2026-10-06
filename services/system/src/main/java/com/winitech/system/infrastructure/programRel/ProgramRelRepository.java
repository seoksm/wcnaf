package com.winitech.system.infrastructure.programRel;

import com.winitech.system.domain.organization.Organization;
import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.programRel.ProgramRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.programRel
 * └ ProgramRelRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 09:06
 **/
public interface ProgramRelRepository extends JpaRepository<ProgramRel, UUID> {
	List<ProgramRel> findAllByProgramId(UUID programId);

	List<ProgramRel> findAllByProgramIdIn(List<UUID> programIdList);

	@Query("" +
			"SELECT programRel.relProgram " +
			"  FROM ProgramRel programRel" +
			" WHERE programRel.program.id = :programId" +
			" ORDER BY programRel.relProgram.programCode, programRel.relProgram.programName")
	List<Program> findAllRelProgramByProgramId(@Param("programId") UUID programId);

	List<ProgramRel> findAllByRelProgramId(UUID programId);
}
