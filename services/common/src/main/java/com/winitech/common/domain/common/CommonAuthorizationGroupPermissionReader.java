package com.winitech.common.domain.common;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupPermissionReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:43
 **/
public interface CommonAuthorizationGroupPermissionReader {
	CommonAuthorizationGroupPermission getCommonAuthorizationGroupPermissionById(UUID authorizationGroupId, UUID menuId);

	CommonAuthorizationGroupPermission getCommonAuthorizationGroupPermissionByIdIfExists(UUID authorizationGroupId, UUID menuId);

//	List<CommonAuthorizationGroupPermission> getCommonAuthorizationGroupPermissionByName(String name);

	List<CommonAuthorizationGroupPermission> getAllCommonAuthorizationGroupPermission();

	boolean isExistCommonAuthorizationGroupPermissionById(UUID authorizationGroupId, UUID menuId);

	List<CommonAuthorizationGroupPermission> getListByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList);
}