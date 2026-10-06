package com.winitech.apiGateway.domain.userSessionBlock;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.apiGateway.domain.userSessionBlock
 * └ UserSessionBlock.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:03
 **/
@Getter
public class UserSessionBlockInfo {
	private final UUID id;
	private final UUID userSessionId;
	private final OffsetDateTime accessTokenExpiresAt;
	private final UUID userId;

	public UserSessionBlockInfo(UserSessionBlock userSessionBlock) {
		this.id = userSessionBlock.getId();
		this.userSessionId = userSessionBlock.getUserSessionId();
		this.accessTokenExpiresAt = userSessionBlock.getAccessTokenExpiresAt();
		this.userId = userSessionBlock.getUserId();
	}
}
