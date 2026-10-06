package com.winitech.common.domain.common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Id;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupUserCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:43
 **/
@Getter
@Builder
@ToString
public class CommonAuthorizationGroupUserCommand {
	private final UUID userId;
	private final UUID authorizationGroupId;

	public CommonAuthorizationGroupUser toEntity() {
		return CommonAuthorizationGroupUser.builder()
				.userId(userId)
				.authorizationGroupId(authorizationGroupId)
				.build();
	}
}
