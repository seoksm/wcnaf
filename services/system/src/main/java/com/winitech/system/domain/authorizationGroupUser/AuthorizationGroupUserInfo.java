package com.winitech.system.domain.authorizationGroupUser;

import com.winitech.system.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.authorizationGroupUser
 * └ AuthorizationGroupUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-20 10:09
 **/
@Getter
public class AuthorizationGroupUserInfo {
	private final UUID id;
	private final UUID authorizationGroupId;
	private final UUID userId;

	public AuthorizationGroupUserInfo(AuthorizationGroupUser authorizationGroupUser) {
		this.id = authorizationGroupUser.getId();
		this.authorizationGroupId = authorizationGroupUser.getAuthorizationGroup().getId();
		this.userId = authorizationGroupUser.getUser().getId();
	}
	
	@Getter
	@Setter
	public static class PageInfo {
		private UUID id;
		private UUID authorizationGroupId;
		private UUID userId;
		private String username;
		private String userDepartmentName;
		private String fullName;
		private User.Status status;

		private String authorizationGroupUserStatus;
		
		public PageInfo() {
		}
	}
}
