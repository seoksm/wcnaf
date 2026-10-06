package com.winitech.system.infrastructure.program;

import com.winitech.system.domain.program.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.interfaces.program
 * ㄴ ProgramRepository.java
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 13:14
 * @see : None
 **/
public interface ProgramRepository extends JpaRepository<Program, UUID> {
    List<Program> getAllByParentProgram(Program program);
}
