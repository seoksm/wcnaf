package com.winitech.common.domain.common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonUserCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 12:41
 **/
@Getter
@Builder
@ToString
public class CommonUserCommand {
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

	public CommonUser toEntity() {
		return CommonUser.builder()
				.id(id)
				.username(username)
				.firstName(firstName)
				.lastName(lastName)
				.fullName(fullName)
				.email(email)
				.phoneNumber(phoneNumber)
				.employeeNo(employeeNo)
				.dutyName(dutyName)
				.departmentName(departmentName)
				.status(status)
				.build();
	}
}
