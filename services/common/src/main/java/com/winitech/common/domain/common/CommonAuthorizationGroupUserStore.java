package com.winitech.common.domain.common;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupUserStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:44
 **/
public interface CommonAuthorizationGroupUserStore {
	CommonAuthorizationGroupUser store(CommonAuthorizationGroupUser commonAuthorizationGroupUser);

	CommonAuthorizationGroupUser modify(CommonAuthorizationGroupUser commonAuthorizationGroupUser, CommonAuthorizationGroupUserCommand command);

	void storeAll(List<CommonAuthorizationGroupUser> toSaveList);

	void remove(UUID userId, UUID authorizationGroupId);

	void removeAll(List<CommonAuthorizationGroupUser> toDeleteList);
}
