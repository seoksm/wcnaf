package com.winitech.system.domain.program;

import com.winitech.common.library.commonType.WiniPageInfo;

import java.util.List;
import java.util.UUID;

/**
* com.winitech.system.domain.program
* ㄴ ProgramService.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 2:28
* @see : None
 **/
public interface ProgramService {
    ProgramInfo registerProgram(ProgramCommand.RegisterRequestCommand programCommand);
    ProgramInfo modifyProgram(UUID programId, ProgramCommand.ModifyRequestCommand programCommand);
    ProgramInfo.DetailInfo searchProgram(UUID programId);
    void removeProgram(UUID programId);
    List<ProgramInfo> getAllProgram();
    WiniPageInfo<ProgramInfo> getProgramPage(Integer page, Integer pageSize, String searchType, String searchKeyword, Program.MenuStatus menuStatus);
}
