package com.winitech.common.interfaces.inboundAdapter.web;

import com.winitech.common.domain.common.CommonUser;
import com.winitech.common.domain.common.CommonUserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.NonNull;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.web
 * └ CommonUserDto.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-14 13:56
 **/
@ApiModel("Common User Dto")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommonUserDto {
	@ApiModelProperty(value = "사용자 고유번호")
	private UUID id;
	@ApiModelProperty(value = "로그인 아이디")
	private String username;
	@ApiModelProperty(value = "사용자 이름 (first name)")
	private String firstName;
	@ApiModelProperty(value = "사용자 성씨 (surname)")
	private String lastName;
	@ApiModelProperty(value = "사용자 전체 이름")
	private String fullName;
	@ApiModelProperty(value = "이메일")
	private String email;
	@ApiModelProperty(value = "전화번호")
	private String phoneNumber;
	@ApiModelProperty(value = "사원번호")
	private String employeeNo;
	@ApiModelProperty(value = "직급")
	private String dutyName;
	@ApiModelProperty(value = "부서명")
	private String departmentName;
	@ApiModelProperty(value = "사용자 상태")
	private CommonUser.Status status;
	
	public CommonUserDto(CommonUserInfo user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.fullName = user.getFullName();
		this.email = user.getEmail();
		this.phoneNumber = user.getPhoneNumber();
		this.employeeNo = user.getEmployeeNo();
		this.dutyName = user.getDutyName();
		this.departmentName = user.getDepartmentName();
		this.status = user.getStatus();
	}
}
