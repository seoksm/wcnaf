package com.winitech.system.domain.program;

import com.winitech.system.domain.programRel.ProgramRel;

import java.util.List;
import java.util.UUID;

/**
* com.winitech.system.domain.program
* ㄴ ProgramStore.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 2:16
* @see : None
 **/
public interface ProgramStore {
    Program store(Program program);
    Program modify(Program program, ProgramCommand.ModifyRequestCommand programCommand);
    void remove(UUID programId);
}
