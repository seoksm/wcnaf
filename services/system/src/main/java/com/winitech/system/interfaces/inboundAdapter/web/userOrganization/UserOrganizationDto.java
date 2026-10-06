package com.winitech.system.interfaces.inboundAdapter.web.userOrganization;

import com.winitech.system.domain.user.UserInfo;
import com.winitech.system.domain.userOrganization.UserOrganizationCommand;
import com.winitech.system.domain.userOrganization.UserOrganizationInfo;
import com.winitech.system.interfaces.inboundAdapter.web.user.UserDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * com.winitech.system.interfaces.inboundAdapter.web.userOrganization
 * └ UserOrganizationDto.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
@NoArgsConstructor
public class UserOrganizationDto {
	@ApiModel("조직에 사용자 등록 요청")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RegisterUserOrganizationRequest {
		@ApiModelProperty(value = "사용자 그룹 코드", example = "ADMIN", required = true)
		private String userGroupCode;
		
		public UserOrganizationCommand toCommand() {
			return UserOrganizationCommand
					.builder()
					.userGroupCode(userGroupCode)
					.build();
		}
	}

	@ApiModel("UserOrganization management response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserOrganizationResponse {
		@ApiModelProperty(value = "사용자 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
		private String userId;
		@ApiModelProperty(value = "조직 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
		private String organizationId;
		@ApiModelProperty(value = "조직 이름", example = "(주)위니텍", required = true)
		private String organizationName;
		@ApiModelProperty(value = "그룹 ID", example = "1", required = true)
		private String userGroupId;
		@ApiModelProperty(value = "그룹 코드", example = "ADMIN", required = true)
		private String userGroupCode;
		@ApiModelProperty(value = "그룹 이름", example = "관리자", required = true)
		private String userGroupName;

		public UserOrganizationResponse(UserOrganizationInfo userOrganizationInfo) {
			this.userId = userOrganizationInfo.getUserId().toString();
			this.organizationId = userOrganizationInfo.getOrganizationId().toString();
			this.organizationName = userOrganizationInfo.getOrganizationName();
			this.userGroupId = null;
		}
	}

	@ApiModel("UserOrganization response for login")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserOrganizationLoginResponse {
		@ApiModelProperty(value = "조직 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
		private String organizationId;
		@ApiModelProperty(value = "조직 코드", example = "WINITECH", required = true)
		private String organizationCode;
		@ApiModelProperty(value = "조직 이름", example = "(주)위니텍", required = true)
		private String organizationName;
		@ApiModelProperty(value = "그룹 ID", example = "1", required = true)
		private String userGroupId;
		@ApiModelProperty(value = "그룹 코드", example = "ADMIN", required = true)
		private String userGroupCode;
		@ApiModelProperty(value = "그룹 이름", example = "관리자", required = true)
		private String userGroupName;

		public UserOrganizationLoginResponse(UserOrganizationInfo userOrganizationInfo) {
			this.organizationId = userOrganizationInfo.getOrganizationId().toString();
			this.organizationCode = userOrganizationInfo.getOrganizationCode();
			this.organizationName = userOrganizationInfo.getOrganizationName();
			this.userGroupId = null;
		}
	}

	@ApiModel("UserOrganization management response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserOrganizationWithUserInfoResponse {
		@ApiModelProperty(value = "사용자 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
		private String userId;
		@ApiModelProperty(value = "조직 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
		private String organizationId;
		@ApiModelProperty(value = "조직 이름", example = "(주)위니텍", required = true)
		private String organizationName;
		@ApiModelProperty(value = "그룹 ID", example = "1", required = true)
		private String userGroupId;
		@ApiModelProperty(value = "그룹 코드", example = "ADMIN", required = true)
		private String userGroupCode;
		@ApiModelProperty(value = "그룹 이름", example = "관리자", required = true)
		private String userGroupName;
		@ApiModelProperty(value = "사용자 정보")
		private UserDto.UserResponse userInfo;

		public UserOrganizationWithUserInfoResponse(UserOrganizationInfo userOrganizationInfo, UserInfo userInfo) {
			this.userId = userOrganizationInfo.getUserId().toString();
			this.organizationId = userOrganizationInfo.getOrganizationId().toString();
			this.organizationName = userOrganizationInfo.getOrganizationName();
			this.userGroupId = null;
		
			if (userInfo == null) {
				this.userInfo = null;
			} else {
				this.userInfo = new UserDto.UserResponse(userInfo);
			}
		}
	}
}
