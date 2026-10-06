package com.winitech.apiGateway.domain.userSessionBlock;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.userSessionBlock
 * └ UserSessionBlock.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:03
 **/
@Getter
@Builder
@ToString
public class UserSessionBlockCommand {
	private final UUID id;
	private final UUID userSessionId;
	private final OffsetDateTime accessTokenExpiresAt;
	private final UUID userId;

	public UserSessionBlock toEntity() {
		return UserSessionBlock.builder()
			.id(id)
			.userSessionId(userSessionId)
			.accessTokenExpiresAt(accessTokenExpiresAt)
			.userId(userId)
			.build();
	}

	@Getter
	@Builder
	@ToString
	public static class RegisterRequestCommand {
		private final UUID userSessionId;
		private final OffsetDateTime accessTokenExpiresAt;
		private final UUID userId;
	}

	@Getter
	@Builder
	@ToString
	public static class ModifyRequestCommand {
		private final UUID userSessionId;
		private final OffsetDateTime accessTokenExpiresAt;
		private final UUID userId;
	}
}
