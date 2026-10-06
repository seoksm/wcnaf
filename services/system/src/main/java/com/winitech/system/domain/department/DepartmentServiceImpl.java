package com.winitech.system.domain.department;

import java.util.*;
import java.util.stream.Collectors;

import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.core.CircularReferenceDetector;
import com.winitech.system.application.department.DepartmentFacade;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* com.winitech.user.domain
* ㄴ UserServiceImpl.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 5:31
* @see : None
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl extends EgovAbstractServiceImpl implements DepartmentService {
    //사용자 저장
    private final DepartmentStore departmentStore;
    //사용자 조회
    private final DepartmentReader departmentReader;

    /**
     * email 계정이 중복값이 없으면
     * 비밀번호를 암호화하여 (평문 패스워드 BCrypt 해싱)
     * 새로운 사용자를 추가한다.
     */
    @Override
    public DepartmentInfo registerDepartment(DepartmentCommand command) {
        Department initDepartment = command.toEntity(departmentReader);
        departmentStore.store(initDepartment);
        return new DepartmentInfo(initDepartment);
    }

    @Override
    public DepartmentInfo modifyDepartment(UUID departmentId, DepartmentCommand command) {
    	Department Department = departmentStore.modify(departmentId, command);
        return new DepartmentInfo(Department);
    }

    @Override
    public void modifyDepartmentOrder(List<DepartmentCommand.OrderModifyRequestCommand> commandList) {
        if (commandList == null || commandList.size() == 0) {
            return;
        }

        // 번호 부여
        int sortSeq = 1;

        if (commandList.get(0).getParentDepartmentId() != null) {
            Department topParentDepartment = departmentReader.getDepartment(commandList.get(0).getParentDepartmentId());

            sortSeq = topParentDepartment.getSortSeq();
        }

        // 메뉴 순서 변경
        for (DepartmentCommand.OrderModifyRequestCommand command : commandList) {
            departmentStore.updateParentAndSortSeq(command.getDepartmentId(), command.getParentDepartmentId(), sortSeq);

            // 가장 
            sortSeq++;
        }

        departmentStore.flush();

        Map<UUID, Set<UUID>> childrenMap = new HashMap<>();

        departmentReader.getAllDepartment().forEach(menu -> {
            if (menu.getParentDepartment() != null) {
                Set<UUID> children = childrenMap.get(menu.getParentDepartment().getId());

                if (children == null) {
                    children = new HashSet<>();
                    childrenMap.put(menu.getParentDepartment().getId(), children);
                }

                children.add(menu.getId());
            }
        });

        // 순환 참조 확인
        UUID circularReferencedNodeId = CircularReferenceDetector.findFirstCyclicNode(childrenMap);
        if (circularReferencedNodeId != null) {
            log.error("A circular reference was detected. (menuId : {})", circularReferencedNodeId);
            throw new IllegalStatusException("A circular reference was detected.");
        }
    }

    @Override
    public void removeDepartment(UUID departmentId) {
    	departmentStore.remove(departmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentInfo searchDepartmentInfo(UUID departmentId) {
        Department Department = departmentReader.getDepartment(departmentId);
        return new DepartmentInfo(Department);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DepartmentInfo searchDepartmentByCode(String departmentCode) {
    	Department department = departmentReader.getDepartmentByCode(departmentCode);
    	return new DepartmentInfo(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentInfo> searchAllDepartmentInfo() {
        List<Department> departmentList = departmentReader.getAllDepartment();
        return departmentList.stream().map(DepartmentInfo::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentInfo.DepartmentTreeInfo> searchDepartmentTree() {
        List<Department> departmentList = departmentReader.getAllDepartmentTree();
        return departmentList.stream()
                .filter(department -> department.getParentDepartment() == null)
                .map(DepartmentInfo.DepartmentTreeInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentInfo> searchByParentDepartmentId(UUID parentDepartmentId) {
        Department parentDepartment = departmentReader.getDepartment(parentDepartmentId);
        List<Department> departmentList = parentDepartment.getChildrenDepartment();
        
        return departmentList.stream().map(DepartmentInfo::new).collect(Collectors.toList());
    }
}
