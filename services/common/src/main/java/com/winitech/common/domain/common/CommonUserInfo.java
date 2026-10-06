package com.winitech.common.domain.common;

import lombok.Getter;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonUserInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 12:43
 **/
@Getter
public class CommonUserInfo {
	private final UUID id;
	private final String username;
	private final String firstName;
	private final String lastName;
	private final String fullName;
	
	private final String email;
	private final String phoneNumber;
	private final String employeeNo;
	private final String dutyName;
	private final String departmentName;
	
	private final CommonUser.Status status;

	public CommonUserInfo(CommonUser commonUser) {
		this.id = commonUser.getId();
		this.username = commonUser.getUsername();
		this.firstName = commonUser.getFirstName();
		this.lastName = commonUser.getLastName();
		this.fullName = commonUser.getFullName();
		
		this.email = commonUser.getEmail();
		this.phoneNumber = commonUser.getPhoneNumber();
		this.employeeNo = commonUser.getEmployeeNo();
		this.dutyName = commonUser.getDutyName();
		this.departmentName = commonUser.getDepartmentName();
		
		this.status = commonUser.getStatus();
	}
}
