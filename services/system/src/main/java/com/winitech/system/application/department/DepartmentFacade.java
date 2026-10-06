package com.winitech.system.application.department;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.winitech.system.domain.department.DepartmentCommand;
import com.winitech.system.domain.department.DepartmentInfo;
import com.winitech.system.domain.department.DepartmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* com.winitech.system.application.userGroup
* ㄴ UserGroupFacade.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 6:15
* @see : None
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentFacade {

    private final DepartmentService departmentService;
    
    public DepartmentInfo registerDepartment(DepartmentCommand command) {
        return departmentService.registerDepartment(command);
    }
    public DepartmentInfo modifyDepartment(UUID departmentId, DepartmentCommand command) {
        return departmentService.modifyDepartment(departmentId, command);

    }
    public DepartmentInfo searchDepartmentInfo(UUID departmentId) {
        return departmentService.searchDepartmentInfo(departmentId);
    }

    public void removeDepartment(UUID departmentId) {
        departmentService.removeDepartment(departmentId);
    }

    public List<DepartmentInfo> searchAllDepartment() {
        return departmentService.searchAllDepartmentInfo();
    }

    public List<DepartmentInfo.DepartmentTreeInfo> searchDepartmentTree() {
        return departmentService.searchDepartmentTree();
    }

    public DepartmentInfo searchByDepartmentCode(String departmentCode) {
        return departmentService.searchDepartmentByCode(departmentCode);
    }
    
    public List<DepartmentInfo> searchByParentDepartmentId(UUID parentDepartmentId) {
        return departmentService.searchByParentDepartmentId(parentDepartmentId);
    }

    public void modifyDepartmentOrder(List<DepartmentCommand.OrderModifyRequestCommand> commandList) {
        departmentService.modifyDepartmentOrder(commandList);
    }
}
