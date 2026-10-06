package com.winitech.system.domain.department;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.winitech.system.domain.department.Department.SystemStatus;

import lombok.Getter;

/**
* com.winitech.user.domain
* ㄴ UserInfo.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 4:28
* @see : None
 **/
@Getter
public class DepartmentInfo {
    private final UUID id;
    private final String departmentCode;
    private final String departmentName;
    private final UUID parentDepartmentId;
    private final Integer sortSeq;
    private final Department.Status status;

    public DepartmentInfo(Department department) {
        this.id = department.getId();
        this.departmentCode = department.getDepartmentCode();
        this.departmentName = department.getDepartmentName();
        this.parentDepartmentId = department.getParentDepartment() == null ? null : department.getParentDepartment().getId();
        this.sortSeq = department.getSortSeq();
        this.status = department.getStatus();
    }

    @Getter
    public static class DepartmentTreeInfo {
        private final UUID id;
        private final String departmentCode;
        private final String departmentName;
        private final UUID parentDepartmentId;
        private final Integer sortSeq;
        private final Department.Status status;
        private final List<DepartmentTreeInfo> childrenDepartment;

        public DepartmentTreeInfo(Department department) {
            this.id = department.getId();
            this.departmentCode = department.getDepartmentCode();
            this.departmentName = department.getDepartmentName();
            this.parentDepartmentId = department.getParentDepartment() == null ? null : department.getParentDepartment().getId();
            this.sortSeq = department.getSortSeq();
            this.status = department.getStatus();
            
            this.childrenDepartment = department.getChildrenDepartment().stream()
                    .map(DepartmentTreeInfo::new)
                    .collect(Collectors.toList());
        }
    }
}
