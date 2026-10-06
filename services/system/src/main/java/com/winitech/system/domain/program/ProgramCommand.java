package com.winitech.system.domain.program;

import lombok.*;

import java.util.List;
import java.util.UUID;

/**
* 'com.winitech.system.domain.program'
* ㄴ ProgramCommand.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 2:56
* @see : None
 **/
@Getter
@Builder
@ToString
public class ProgramCommand {
    private final UUID id;
    private final UUID parentProgramId;
    private final String programCode;
    private final String programName;
    private final String programMapping;
    private final Program.Status status;
    private final Program.MenuStatus menuStatus;
    private final Program.MobileStatus mobileStatus;
    private final Program.ProgramMappingStatus programMappingStatus;
    private final String remark;
    
    @Getter
    @Builder    
    @ToString
    public static class RegisterRequestCommand {
        @Setter
        private UUID id;
        private final UUID parentProgramId;
        private final String programCode;
        private final String programName;
        private final String programMapping;
        private final Program.Status status;
        private final Program.MenuStatus menuStatus;
        private final Program.MobileStatus mobileStatus;
        private final Program.ProgramMappingStatus programMappingStatus;
        private final String remark;
        
        private final List<UUID> relProgramIdList;

        public Program toEntity(Program parentProgram) {
            return Program.builder()
                    .id(id)
                    .parentProgram(parentProgram)
                    .programCode(programCode)
                    .programName(programName)
                    .programMapping(programMapping)
                    .status(status)
                    .menuStatus(menuStatus)
                    .mobileStatus(mobileStatus)
                    .programMappingStatus(programMappingStatus)
                    .remark(remark)
                    .build();
        }
    }
    
    @Getter
    @Builder    
    @ToString
    public static class ModifyRequestCommand {
        @Setter
        private UUID id;
        private final UUID parentProgramId;
        private final String programCode;
        private final String programName;
        private final String programMapping;
        private final Program.Status status;
        private final Program.MenuStatus menuStatus;
        private final Program.MobileStatus mobileStatus;
        private final Program.ProgramMappingStatus programMappingStatus;
        private final String remark;
        
        private final List<UUID> relProgramIdList;
    }
}
