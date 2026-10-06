package com.winitech.system.domain.authorizationGroupUser;

import com.winitech.common.library.commonType.WiniPageInfo;

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
public interface AuthorizationGroupUserService {
	AuthorizationGroupUserInfo registerAuthorizationGroupUser(UUID authorizationGroupId, AuthorizationGroupUserCommand.RegisterRequestCommand authorizationGroupUserCommand);
	List<AuthorizationGroupUserInfo> registerAuthorizationGroupUserBatch(UUID authorizationGroupId, List<AuthorizationGroupUserCommand.BatchRegisterRequestCommand> commands);
	void removeAuthorizationGroupUser(UUID authorizationGroupUserId);
	void removeAuthorizationGroupUser(UUID authorizationGroupId, UUID userId);
	AuthorizationGroupUserInfo searchAuthorizationGroupUserById(UUID id);
	AuthorizationGroupUserInfo searchAuthorizationGroupUser(UUID authorizationGroupId, UUID userId);
	List<AuthorizationGroupUserInfo> getAllAuthorizationGroupUser();
	WiniPageInfo<AuthorizationGroupUserInfo.PageInfo> searchAuthorizationGroupUserPage(UUID authorizationGroupId, String status, String authorizationGroupUserStatus, Integer page, Integer pageSize, String searchType, String searchKeyword);

	List<AuthorizationGroupUserInfo> searchAuthorizationGroupUserByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList);
}
