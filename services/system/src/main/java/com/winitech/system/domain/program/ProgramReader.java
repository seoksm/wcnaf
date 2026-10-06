package com.winitech.system.domain.program;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
* com.winitech.system.domain.program
* ㄴ ProgramReader.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 3:03
* @see : None
 **/
public interface ProgramReader {
    Program getProgramById(UUID programId);
	
    List<Program> getAllProgram();

	Page<ProgramInfo> getProgramPage(Integer page, Integer pageSize, String searchType, String searchKeyword, Program.MenuStatus menuStatus);
}
