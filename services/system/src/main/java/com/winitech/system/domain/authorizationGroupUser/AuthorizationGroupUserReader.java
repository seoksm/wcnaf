package com.winitech.system.domain.authorizationGroupUser;

import com.winitech.common.library.commonType.WiniPageInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.authorizationGroupUser
 * └ AuthorizationGroupUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-20 10:09
 **/
public interface AuthorizationGroupUserReader {
	AuthorizationGroupUser getAuthorizationGroupUserById(UUID authorizationGroupUserId);
	// AuthorizationGroupUser getAuthorizationGroupUserByAuthorizationGroupUserCode(String authorizationGroupUserCode);
	List<AuthorizationGroupUser> getAllAuthorizationGroupUser();
	AuthorizationGroupUser getAuthorizationGroupUser(UUID authorizationGroupId, UUID userId);
	Page<AuthorizationGroupUserInfo.PageInfo> getAuthorizationGroupUserPage(UUID authorizationGroupId, String status, String authorizationGroupUserStatus, Integer page, Integer pageSize, String searchType, String searchKeyword);

	List<AuthorizationGroupUser> getAuthorizationGroupUserByAuthorizationGroupId(UUID authorizationGroupId);
	List<AuthorizationGroupUser> getAuthorizationGroupUserByAuthorizationGroupIdAndUserIdList(UUID authorizationGroupId, List<UUID> userIdList);

	boolean existAuthorizationGroupUserByAuthorizationGroupIdAndUserIdAndExcludingSelf(UUID authorizationGroupId, UUID userId, UUID authorizationGroupUserId);

	List<AuthorizationGroupUser> getAuthorizationGroupUserByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList);
}
