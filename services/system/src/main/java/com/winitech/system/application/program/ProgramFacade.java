package com.winitech.system.application.program;

import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.program.ProgramCommand;
import com.winitech.system.domain.program.ProgramInfo;
import com.winitech.system.domain.program.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
* com.winitech.system.application.program
* ㄴ ProgramFacade.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 5:51
* @see : None
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramFacade {
    private final ProgramService programService;
    
    public ProgramInfo registerProgram(ProgramCommand.RegisterRequestCommand programCommand) {
        ProgramInfo programInfo = programService.registerProgram(programCommand);
        return programInfo;
    }
    public ProgramInfo modifyProgram(UUID programId, ProgramCommand.ModifyRequestCommand programCommand){
        ProgramInfo programInfo = programService.modifyProgram(programId, programCommand);
        return programInfo;
    }
    public ProgramInfo.DetailInfo searchProgram(UUID programId) {
        ProgramInfo.DetailInfo programInfo = programService.searchProgram(programId);

        return programInfo;
    }
    public void removeProgram(UUID programId) {
        programService.removeProgram(programId);
    }
    
    public List<ProgramInfo> getAllProgram() {
        List<ProgramInfo> programInfoList = programService.getAllProgram();
        return programInfoList;
    }
    
    public WiniPageInfo<ProgramInfo> getAllProgram(Integer page, Integer pageSize, String searchType, String searchKeyword, Program.MenuStatus menuStatus) {
        WiniPageInfo<ProgramInfo> programInfoList = programService.getProgramPage(page, pageSize, searchType, searchKeyword, menuStatus);
        return programInfoList;
    }
}
