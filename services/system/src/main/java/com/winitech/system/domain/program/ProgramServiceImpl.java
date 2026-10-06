package com.winitech.system.domain.program;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.menu.MenuService;
import com.winitech.system.domain.programRel.ProgramRelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
* com.winitech.system.domain.program
* ㄴ ProgramServiceImpl.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 1:37
* @see : None
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramServiceImpl extends EgovAbstractServiceImpl implements ProgramService{
    //프로그램 저장
    private final ProgramStore programStore;
    //프로그램 조회
    private final ProgramReader programReader;
    
    private final MenuService menuService;

    private final ProgramRelService programRelService;

    @Override
    @Transactional
    public ProgramInfo registerProgram(ProgramCommand.RegisterRequestCommand programCommand) {
        Program parentProgram = null;
        if(programCommand.getParentProgramId() != null) {
            try {
                parentProgram = programReader.getProgramById(programCommand.getParentProgramId());
            } catch (EntityNotFoundException e) {
                throw new IllegalStatusException("부모 프로그램이 존재하지 않습니다.");
            }
        }

        Program entity = programCommand.toEntity(parentProgram);

        // 프로그램 등록
        Program program = programStore.store(entity);

        // 관련 프로그램 목록 등록
        programRelService.saveProgramRelList(program.getId(), programCommand.getRelProgramIdList());
        
        return new ProgramInfo(program);
    }

    @Override
    @Transactional
    public ProgramInfo modifyProgram(UUID programId, ProgramCommand.ModifyRequestCommand programCommand) {
        Program parentProgram = null;
        if(programCommand.getParentProgramId() != null) {
            try {
                parentProgram = programReader.getProgramById(programCommand.getParentProgramId());
            } catch (EntityNotFoundException e) {
                throw new IllegalStatusException("부모 프로그램이 존재하지 않습니다.");
            }
        }

        Program modifyProgram = programReader.getProgramById(programId);
        modifyProgram.setParentProgram(parentProgram);

        // 프로그램 수정
        Program program = programStore.modify(modifyProgram, programCommand);

        // 관련 프로그램 목록 수정
        programRelService.saveProgramRelList(program.getId(), programCommand.getRelProgramIdList());

        return new ProgramInfo(program);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramInfo.DetailInfo searchProgram(UUID programId) {
        Program program = programReader.getProgramById(programId);
        
        List<ProgramInfo> programInfoList = programRelService.getRelProgramList(programId);
        
        return new ProgramInfo.DetailInfo(program, programInfoList);
    }

    @Override
    @Transactional
    public void removeProgram(UUID programId) {
        Program program = programReader.getProgramById(programId);

        if (program.getChildrenProgram() != null && program.getChildrenProgram().size() > 0) {
            throw new IllegalStatusException("하위 프로그램을 먼저 삭제해 주세요");
        }
        
        if (menuService.existsMenuByProgramId(programId)) {
            throw new IllegalStatusException("메뉴에 등록된 프로그램은 삭제할 수 없습니다.");
        }
        
        // 삭제된 메뉴에서 사용중인 경우 프로그램 ID 삭제
        menuService.unlinkDeletedMenuWithProgramId(programId);
        
        // 현재 프로그램의 관련 프로그램 목록 삭제
        programRelService.removeByProgramId(programId);

        // 다른 프로그램의 관련 프로그램 목록에 있는 경우 삭제
        programRelService.removeByRelProgramId(programId);
    
        programStore.remove(programId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramInfo> getAllProgram() {
        List<Program> programList = programReader.getAllProgram();
        return programList.stream().map(ProgramInfo::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WiniPageInfo<ProgramInfo> getProgramPage(Integer page, Integer pageSize, String searchType, String searchKeyword, Program.MenuStatus menuStatus) {
        Page<ProgramInfo> programPage = programReader.getProgramPage(page, pageSize, searchType, searchKeyword, menuStatus);

		return new WiniPageInfo<>(programPage);        
    }
}
