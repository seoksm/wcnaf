package com.winitech.system.application.authorizationGroupUser;

import com.winitech.common.domain.common.CommonAuthorizationGroupUserInfo;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserCommand;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserInfo;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserService;
import com.winitech.system.interfaces.inboundAdapter.web.authorizationGroupUser.AuthorizationGroupUserProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.authorizationGroupUser
 * └ AuthorizationGroupUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-20 10:09
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationGroupUserFacade {
	private final AuthorizationGroupUserService authorizationGroupUserService;
	
	private final AuthorizationGroupUserProducer authorizationGroupUserProducer;

	public AuthorizationGroupUserInfo registerAuthorizationGroupUser(UUID authorizationGroupId, AuthorizationGroupUserCommand.RegisterRequestCommand authorizationGroupUserCommand) {
		AuthorizationGroupUserInfo result = authorizationGroupUserService.registerAuthorizationGroupUser(authorizationGroupId, authorizationGroupUserCommand);

		syncAuthorizationGroupUser(Arrays.asList(authorizationGroupId));

		return result;
	}

	public void removeAuthorizationGroupUser(UUID authorizationGroupUserId) {
		AuthorizationGroupUserInfo authorizationGroupUserInfo = authorizationGroupUserService.searchAuthorizationGroupUserById(authorizationGroupUserId);

		authorizationGroupUserService.removeAuthorizationGroupUser(authorizationGroupUserId);

		syncAuthorizationGroupUser(Arrays.asList(authorizationGroupUserInfo.getAuthorizationGroupId()));
	}
	
	public void removeAuthorizationGroupUser(UUID authorizationGroupId, UUID userId) {
		authorizationGroupUserService.removeAuthorizationGroupUser(authorizationGroupId, userId);

		syncAuthorizationGroupUser(Arrays.asList(authorizationGroupId));
	}

	public AuthorizationGroupUserInfo searchAuthorizationGroupUser(UUID authorizationGroupId, UUID userId) {
		return authorizationGroupUserService.searchAuthorizationGroupUser(authorizationGroupId, userId);
	}

	public AuthorizationGroupUserInfo searchAuthorizationGroupUserById(UUID id) {
		return authorizationGroupUserService.searchAuthorizationGroupUserById(id);
	}

	public List<AuthorizationGroupUserInfo> getAllAuthorizationGroupUser() {
		return authorizationGroupUserService.getAllAuthorizationGroupUser();
	}

	public WiniPageInfo<AuthorizationGroupUserInfo.PageInfo> searchAuthorizationGroupUserPage(UUID authorizationGroupUserId, String status, String authorizationGroupUserStatus, Integer page, Integer pageSize, String searchType, String searchKeyword) {
		return authorizationGroupUserService.searchAuthorizationGroupUserPage(authorizationGroupUserId, status, authorizationGroupUserStatus, page, pageSize, searchType, searchKeyword);
	}

	public List<AuthorizationGroupUserInfo> registerAuthorizationGroupUserBatch(UUID authorizationGroupId, List<AuthorizationGroupUserCommand.BatchRegisterRequestCommand> commands) {
		List<AuthorizationGroupUserInfo> result = authorizationGroupUserService.registerAuthorizationGroupUserBatch(authorizationGroupId, commands);
		
		syncAuthorizationGroupUser(Arrays.asList(authorizationGroupId));
		
		return result;
	}
	
	public void syncAllAuthorizationGroupUser() {
		List<UUID> authorizationGroupIdList = authorizationGroupUserService.getAllAuthorizationGroupUser()
				.stream()
				.map(AuthorizationGroupUserInfo::getAuthorizationGroupId)
				.distinct()
				.collect(Collectors.toList());
		
		syncAuthorizationGroupUser(authorizationGroupIdList);
	}

	private void syncAuthorizationGroupUser(List<UUID> authorizationGroupIdList) {
		List<CommonAuthorizationGroupUserInfo> authorizationGroupUserInfoList = authorizationGroupUserService.searchAuthorizationGroupUserByAuthorizationGroupIdList(authorizationGroupIdList)
				.stream()
				.map(it -> 
						CommonAuthorizationGroupUserInfo.builder()
								.authorizationGroupId(it.getAuthorizationGroupId())
								.userId(it.getUserId())
								.build()
				)
				.collect(Collectors.toList());
		
		authorizationGroupUserProducer.authorizationGroupUserCdc(authorizationGroupIdList, authorizationGroupUserInfoList);
	}
}
