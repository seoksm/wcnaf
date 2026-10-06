package com.winitech.system.infrastructure.department;

import java.util.UUID;

import com.winitech.common.exception.IllegalStatusException;
import com.winitech.system.domain.department.DepartmentReader;
import com.winitech.system.domain.menu.Menu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.system.domain.department.Department;
import com.winitech.system.domain.department.DepartmentCommand;
import com.winitech.system.domain.department.DepartmentStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* com.winitech.user.infrastructure
* ㄴ UserStoreImpl.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 5:12
* @see : None
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class DepartmentStoreImpl implements DepartmentStore {
    private final DepartmentRepository departmentRepository;
    private final DepartmentReader departmentReader;

    @Override
    public Department store(Department department) {
        if (department.getDepartmentCode() != null) {
            department.setDepartmentCode(department.getDepartmentCode().toUpperCase());
            
            if (departmentReader.existDepartmentByDepartmentCodeAndExcludingSelf(department.getDepartmentCode(), UUID.randomUUID())) {
                throw new IllegalStatusException("Already registered Menu code.");
            }
        }

        if(StringUtils.isEmpty(department.getDepartmentName())) throw new InvalidParamException("department.getDepartmentName()");

        return departmentRepository.save(department);
    }

    @Override
    public Department modify(UUID departmentId, DepartmentCommand command) {
        String departmentCode = command.getDepartmentCode();
        
        if (departmentCode != null) {
            departmentCode = departmentCode.toUpperCase();
            
            if (departmentReader.existDepartmentByDepartmentCodeAndExcludingSelf(departmentCode, departmentId)) {
                throw new IllegalStatusException("Already registered Menu code.");
            }
        }

        if(StringUtils.isEmpty(command.getDepartmentName())) throw new InvalidParamException("command.getDepartmentName()");
        
        Department department = departmentRepository.findById(departmentId).orElseThrow(EntityNotFoundException::new);
        Department parentDepartment = null;
        
        if (command.getParentDepartmentId() != null) {
            parentDepartment = departmentReader.getDepartment(command.getParentDepartmentId());
        }

        department.setDepartmentCode(departmentCode);
        department.setDepartmentName(command.getDepartmentName());
        department.setSortSeq(command.getSortSeq());
        department.setStatus(command.getStatus());
        
        department.setParentDepartment(parentDepartment);
        return departmentRepository.save(department);
    }
    
    @Override
    public void remove(UUID departmentId) {
        Department department = departmentRepository.getById(departmentId);
        
        if (department.getChildrenDepartment().size() > 0) {
            throw new IllegalStatusException("하위 부서가 존재합니다. 먼저 하위 부서를 삭제해주세요.");
        }
        
        department.disable();
        departmentRepository.save(department);
    }

    @Override
    public void updateParentAndSortSeq(UUID departmentId, UUID parentDepartmentId, int sortSeq) {
        Department parentDepartment;

        if (parentDepartmentId == null) {
            parentDepartment = null;
        } else {
            parentDepartment = new Department();
            parentDepartment.setId(parentDepartmentId);
        }

        departmentRepository.updateParentAndSortSeq(departmentId, parentDepartment, sortSeq);

    }

    @Override
    public void flush() {
        departmentRepository.flush();
    }
}
