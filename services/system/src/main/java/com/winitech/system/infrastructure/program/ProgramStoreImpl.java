package com.winitech.system.infrastructure.program;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.program.ProgramCommand;
import com.winitech.system.domain.program.ProgramStore;
import com.winitech.system.domain.programRel.ProgramRel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.interfaces.program
 * ㄴ ProgramStoreImpl.java
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 13:14
 * @see : None
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramStoreImpl implements ProgramStore {
    private final ProgramRepository programRepository;

    @Override
    public Program store(Program program) {
        if(StringUtils.isEmpty(program.getProgramName())) throw new InvalidParamException("program.getProgramName()");
        return programRepository.save(program);
    }

    @Override
    public Program modify(Program program, ProgramCommand.ModifyRequestCommand programCommand) {
        if(StringUtils.isEmpty(programCommand.getProgramName())) throw new InvalidParamException("program.getProgramName()");
        program.setProgramCode(programCommand.getProgramCode());
        program.setProgramName(programCommand.getProgramName());
        program.setProgramMapping(programCommand.getProgramMapping());
        program.setStatus(programCommand.getStatus());
        program.setMenuStatus(programCommand.getMenuStatus());
        program.setMobileStatus(programCommand.getMobileStatus());
        program.setProgramMappingStatus(programCommand.getProgramMappingStatus());
        program.setRemark(programCommand.getRemark());
        return programRepository.save(program);
    }

    @Override
    public void remove(UUID programId) {
        programRepository.deleteById(programId);
    }
}
