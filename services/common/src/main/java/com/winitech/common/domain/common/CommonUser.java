package com.winitech.common.domain.common;

import com.winitech.common.domain.AbstractEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 12:36
 **/
@Slf4j
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "common_user")
public class CommonUser extends AbstractEntity {
	@Id
	protected UUID id;
	@NonNull
	protected String username;
	@NonNull
	protected String firstName;
	@NonNull
	protected String lastName;
	@NonNull
	protected String fullName;
	
	@NonNull
	protected String email;
	protected String phoneNumber;
	protected String employeeNo;
	protected String dutyName;
	protected String departmentName;	

	@Enumerated(EnumType.STRING)
	@NonNull
	protected Status status;

	@Getter
	@RequiredArgsConstructor
	public enum Status {
		ENABLE("활성화"), DISABLE("비활성화");
		private final String description;
	}

	public CommonUser(UUID id) {
		this.id = id;
	}

	@Builder
	public CommonUser(UUID id, String username, String firstName, String lastName, String fullName,
					  String email, String phoneNumber, String employeeNo, String dutyName, String departmentName,
					  Status status) {
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = fullName;
		
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.employeeNo = employeeNo;
		this.dutyName = dutyName;
		this.departmentName = departmentName;
		
		this.status = status;
	}

	public void enable() {
		this.status = Status.ENABLE;
	}
	public void disable() {this.status = Status.DISABLE;}
}