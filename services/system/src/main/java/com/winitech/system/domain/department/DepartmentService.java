package com.winitech.system.domain.department;

import java.util.List;
import java.util.UUID;

/**
* com.winitech.user.domain
* ㄴ UserService.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 4:41
* @see : None
 **/
public interface DepartmentService {
    DepartmentInfo registerDepartment(DepartmentCommand departmentCommand);
    DepartmentInfo modifyDepartment(UUID departmentId, DepartmentCommand departmentCommand);
    void modifyDepartmentOrder(List<DepartmentCommand.OrderModifyRequestCommand> commandList);
    void removeDepartment(UUID departmentId);
    DepartmentInfo searchDepartmentInfo(UUID departmentId);
    DepartmentInfo searchDepartmentByCode(String departmentCode);
    List<DepartmentInfo> searchAllDepartmentInfo();
    List<DepartmentInfo.DepartmentTreeInfo> searchDepartmentTree();
    List<DepartmentInfo> searchByParentDepartmentId(UUID parentDepartmentId);
}
