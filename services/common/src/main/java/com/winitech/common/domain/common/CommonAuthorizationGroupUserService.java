package com.winitech.common.domain.common;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupUserService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:44
 **/
public interface CommonAuthorizationGroupUserService {
	CommonAuthorizationGroupUserInfo registerCommonAuthorizationGroupUser(CommonAuthorizationGroupUserCommand command);

	CommonAuthorizationGroupUserInfo modifyCommonAuthorizationGroupUser(UUID userId, UUID authorizationGroupId, CommonAuthorizationGroupUserCommand command);

	void removeCommonAuthorizationGroupUser(UUID userId, UUID authorizationGroupId);

	CommonAuthorizationGroupUserInfo searchCommonAuthorizationGroupUserById(UUID userId, UUID authorizationGroupId);

//	List<CommonAuthorizationGroupUserInfo> searchCommonAuthorizationGroupUserByName(String name);

	List<CommonAuthorizationGroupUserInfo> getAllCommonAuthorizationGroupUser();

	CommonAuthorizationGroupUserInfo saveCommonAuthorizationGroupUser(CommonAuthorizationGroupUserCommand command);

	void syncAll(List<UUID> authorizationGroupIdList, List<CommonAuthorizationGroupUserCommand> commandList);
}
