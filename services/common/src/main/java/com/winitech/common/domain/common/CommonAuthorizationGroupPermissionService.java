package com.winitech.common.domain.common;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupPermissionService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:43
 **/
public interface CommonAuthorizationGroupPermissionService {
	CommonAuthorizationGroupPermissionInfo registerCommonAuthorizationGroupPermission(CommonAuthorizationGroupPermissionCommand command);

	CommonAuthorizationGroupPermissionInfo modifyCommonAuthorizationGroupPermission(UUID authorizationGroupId, UUID menuId, CommonAuthorizationGroupPermissionCommand command);

	void removeCommonAuthorizationGroupPermission(UUID authorizationGroupId, UUID menuId);

	CommonAuthorizationGroupPermissionInfo searchCommonAuthorizationGroupPermissionById(UUID authorizationGroupId, UUID menuId);

//	List<CommonAuthorizationGroupPermissionInfo> searchCommonAuthorizationGroupPermissionByName(String name);

	List<CommonAuthorizationGroupPermissionInfo> getAllCommonAuthorizationGroupPermission();

	CommonAuthorizationGroupPermissionInfo saveCommonAuthorizationGroupPermission(CommonAuthorizationGroupPermissionCommand command);

	void syncAll(List<UUID> authorizationGroupIdList, List<CommonAuthorizationGroupPermissionCommand> commandList);
}
