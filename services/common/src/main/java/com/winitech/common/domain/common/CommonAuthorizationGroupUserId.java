package com.winitech.common.domain.common;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupUserId.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 11:07
 **/
@NoArgsConstructor
public class CommonAuthorizationGroupUserId implements Serializable {
	private UUID userId;
	private UUID authorizationGroupId;

	public CommonAuthorizationGroupUserId(UUID userId, UUID authorizationGroupId) {
		this.userId = userId;
		this.authorizationGroupId = authorizationGroupId;
	}
}
