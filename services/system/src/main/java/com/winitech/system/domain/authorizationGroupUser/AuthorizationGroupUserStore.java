package com.winitech.system.domain.authorizationGroupUser;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.authorizationGroupUser
 * └ AuthorizationGroupUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-20 10:09
 **/
public interface AuthorizationGroupUserStore {
	AuthorizationGroupUser store(AuthorizationGroupUser authorizationGroupUser);
	List<AuthorizationGroupUser> storeAll(List<AuthorizationGroupUser> toSaveList);
	void remove(UUID authorizationGroupUserId);
	void remove(UUID authorizationGroupId, UUID userId);
}
