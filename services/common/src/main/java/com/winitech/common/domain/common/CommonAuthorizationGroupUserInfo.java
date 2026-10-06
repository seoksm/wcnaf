package com.winitech.common.domain.common;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupUserInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:44
 **/
@Getter
public class CommonAuthorizationGroupUserInfo {
	private UUID userId;
	private UUID authorizationGroupId;
	private OffsetDateTime updateAt;

	public CommonAuthorizationGroupUserInfo() {
	}

	public CommonAuthorizationGroupUserInfo(CommonAuthorizationGroupUser commonAuthorizationGroupUser) {
		this.userId = commonAuthorizationGroupUser.getUserId();
		this.authorizationGroupId = commonAuthorizationGroupUser.getAuthorizationGroupId();
		this.updateAt = commonAuthorizationGroupUser.getUpdateAt();
	}
	
	@Builder
	public CommonAuthorizationGroupUserInfo(UUID userId, UUID authorizationGroupId) {
		this.userId = userId;
		this.authorizationGroupId = authorizationGroupId;
	}
}
