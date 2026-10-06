package com.winitech.common.domain.common;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupUserReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:44
 **/
public interface CommonAuthorizationGroupUserReader {
	CommonAuthorizationGroupUser getCommonAuthorizationGroupUserById(UUID userId, UUID authorizationGroupId);

	CommonAuthorizationGroupUser getCommonAuthorizationGroupUserByIdIfExists(UUID userId, UUID authorizationGroupId);

//	List<CommonAuthorizationGroupUser> getCommonAuthorizationGroupUserByName(String name);

	List<CommonAuthorizationGroupUser> getAllCommonAuthorizationGroupUser();

	boolean isExistCommonAuthorizationGroupUserById(UUID authorizationGroupId, UUID menuId);

	List<CommonAuthorizationGroupUser> getListByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList);
}