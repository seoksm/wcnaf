package com.winitech.system.domain.authorizationGroupUser;

import com.winitech.system.domain.authorizationGroup.AuthorizationGroupReader;
import com.winitech.system.domain.user.UserReader;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

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
@Builder
@ToString
public class AuthorizationGroupUserCommand {
	private final UUID id;
	private final UUID authorizationGroupId;
	private final UUID userId;

	public AuthorizationGroupUser toEntity(AuthorizationGroupReader authorizationGroupReader, UserReader userReader) {
		return AuthorizationGroupUser.builder()
			.id(id)
			.authorizationGroup(authorizationGroupReader.getAuthorizationGroup(authorizationGroupId))
			.user(userReader.getUser(userId))
			.build();
	}

	@Getter
	@Builder
	@ToString
	public static class RegisterRequestCommand {
		private final UUID userId;
	}

	@Getter
	@Builder
	@ToString
	public static class ModifyRequestCommand {
		private final UUID userId;
	}

	@Getter
	@Builder
	@ToString
	public static class BatchRegisterRequestCommand {
		private final UUID userId;
		private final AuthorizationGroupUser.AuthorizationGroupUserStatus authorizationGroupUserStatus;
	}
}
