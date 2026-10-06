package com.winitech.system.domain.department;

import java.util.List;
import java.util.UUID;

public interface DepartmentReader {
    Department getDepartment(UUID departmentId);
    List<Department> getAllDepartment();
    List<Department> getAllDepartmentTree();
    Department getDepartmentByCode(String departmentCode);
    List<Department> getUpperDepartment(UUID parentDepartmentId);
    List<Department> existsByDepartmentNameAndUpperDepartment(String deptName, UUID parentDepartmentId);
    boolean existDepartmentByDepartmentCodeAndExcludingSelf(String departmentCode, UUID departmentId);
}
