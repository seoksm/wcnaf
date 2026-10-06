package com.winitech.system.infrastructure.program;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.library.WiniCom;
import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.program.ProgramInfo;
import com.winitech.system.domain.program.ProgramReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.interfaces.program
 * ㄴ ProgramReaderImpl.java
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 13:14
 * @see : None
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramReaderImpl implements ProgramReader {
    private final ProgramRepository programRepository;
    
    private final ProgramQueryRepository programQueryRepository;

    @Override
    public Program getProgramById(UUID programId) {
        return programRepository.findById(programId).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<Program> getAllProgram() {
        return programRepository.findAll();
    }

    @Override
    public Page<ProgramInfo> getProgramPage(Integer page, Integer pageSize, String searchType, String searchKeyword, Program.MenuStatus menuStatus) {
        return programQueryRepository.findAllPage(searchType, searchKeyword, menuStatus, WiniCom.getPageRequest(page, pageSize));
    }
}
