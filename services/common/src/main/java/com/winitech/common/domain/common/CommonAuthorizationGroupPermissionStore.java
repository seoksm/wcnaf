package com.winitech.common.domain.common;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupPermissionStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:43
 **/
public interface CommonAuthorizationGroupPermissionStore {
	CommonAuthorizationGroupPermission store(CommonAuthorizationGroupPermission commonAuthorizationGroupPermission);

	CommonAuthorizationGroupPermission modify(CommonAuthorizationGroupPermission commonAuthorizationGroupPermission, CommonAuthorizationGroupPermissionCommand command);

	void remove(UUID userId, UUID authorizationGroupId);

	void storeAll(List<CommonAuthorizationGroupPermission> toSaveList);

	void removeAll(List<CommonAuthorizationGroupPermission> toDeleteList);
}
