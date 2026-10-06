package com.winitech.system.domain.program;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
* com.winitech.system.domain.program
* ㄴ ProgramInfo.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 2:17
* @see : None
 **/
@Getter
public class ProgramInfo {
    private final UUID id;
    private final String programCode;
    private final String programName;
    private final String programMapping;
    private final Program.Status status;
    private final Program.MenuStatus menuStatus;
    private final Program.MobileStatus mobileStatus;
    private final Program.ProgramMappingStatus programMappingStatus;
    private final UUID parentProgramId;
    private final String remark;

    public ProgramInfo(Program program) {
        this.id = program.getId();
        this.programCode = program.getProgramCode();
        this.programName = program.getProgramName();
        this.programMapping = program.getProgramMapping();
        this.status = program.getStatus();
        this.menuStatus = program.getMenuStatus();
        this.mobileStatus = program.getMobileStatus();
        this.programMappingStatus = program.getProgramMappingStatus();
        this.parentProgramId = program.getParentProgram() == null ? null : program.getParentProgram().getId();
        this.remark = program.getRemark();
    }
    
    @Getter
    public static class DetailInfo extends ProgramInfo {
        private final List<ProgramInfo> relProgramList;
        
        public DetailInfo(Program program, List<ProgramInfo> relProgramList) {
            super(program);
            
            this.relProgramList = relProgramList;
        }        
    }
}
