package com.winitech.system.domain.department;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * com.winitech.system.domain.userOrganization
 * └ UserOrganizationCommand.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2025.02.10
 **/
@Getter
@Builder
@ToString
public class DepartmentCommand {
	private final String departmentCode;
	private final String departmentName;
	private final UUID parentDepartmentId;
	private final Integer sortSeq;
	private final Department.Status status;

    public Department toEntity(DepartmentReader departmentReader) {
		Department department = parentDepartmentId == null ? null : departmentReader.getDepartment(parentDepartmentId);
		
		return Department.builder()
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .parentDepartment(department)
				.sortSeq(sortSeq)
				.status(status)
                .build();
    }

	@Getter
	@Builder
	@ToString
	public static class OrderModifyRequestCommand {
		private final UUID departmentId;
		private final UUID parentDepartmentId;
	}
}
