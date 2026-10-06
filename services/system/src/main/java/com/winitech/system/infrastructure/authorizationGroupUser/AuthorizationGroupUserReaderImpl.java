package com.winitech.system.infrastructure.authorizationGroupUser;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUser;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserInfo;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Comparator;
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
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationGroupUserReaderImpl implements AuthorizationGroupUserReader {
	private final AuthorizationGroupUserRepository authorizationGroupUserRepository;
	private final AuthorizationGroupUserQueryRepository authorizationGroupUserQueryRepository;

	@Override
	public AuthorizationGroupUser getAuthorizationGroupUserById(UUID authorizationGroupUserId) {
		return authorizationGroupUserRepository.findById(authorizationGroupUserId).orElseThrow(EntityNotFoundException::new);
	}

	//@Override
	//public AuthorizationGroupUser getAuthorizationGroupUserByAuthorizationGroupUserCode(String authorizationGroupUserCode) {
	//	return authorizationGroupUserRepository.findByAuthorizationGroupUserCodeAndSystemStatus(authorizationGroupUserCode, AuthorizationGroupUser.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	//}

	@Override
	public List<AuthorizationGroupUser> getAllAuthorizationGroupUser() {
		return authorizationGroupUserRepository.findAll();
	}

	@Override
	public AuthorizationGroupUser getAuthorizationGroupUser(UUID authorizationGroupId, UUID userId) {
		return authorizationGroupUserRepository.findByAuthorizationGroupIdAndUserId(authorizationGroupId, userId).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public Page<AuthorizationGroupUserInfo.PageInfo> getAuthorizationGroupUserPage(UUID authorizationGroupId, String status, String authorizationGroupUserStatus, Integer page, Integer pageSize, String searchType, String searchKeyword) {
		return authorizationGroupUserQueryRepository.findAllPage(authorizationGroupId, status, authorizationGroupUserStatus, searchType, searchKeyword, WiniCom.getPageRequest(page, pageSize));
	}

	@Override
	public List<AuthorizationGroupUser> getAuthorizationGroupUserByAuthorizationGroupId(UUID authorizationGroupId) {
		return authorizationGroupUserRepository.findByAuthorizationGroupId(authorizationGroupId);
	}

	@Override
	public List<AuthorizationGroupUser> getAuthorizationGroupUserByAuthorizationGroupIdAndUserIdList(UUID authorizationGroupId, List<UUID> userIdList) {
		return authorizationGroupUserRepository.findByAuthorizationGroupIdAndUserIdIn(authorizationGroupId, userIdList);
	}

	@Override
	public boolean existAuthorizationGroupUserByAuthorizationGroupIdAndUserIdAndExcludingSelf(UUID authorizationGroupId, UUID userId, UUID authorizationGroupUserId) {
		return authorizationGroupUserRepository.existsByAuthorizationGroupIdAndUserIdAndIdNot(authorizationGroupId, userId, authorizationGroupUserId);
	}

	@Override
	public List<AuthorizationGroupUser> getAuthorizationGroupUserByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList) {
		return authorizationGroupUserRepository.findActiveByAuthorizationGroupIdIn(authorizationGroupIdList);
	}
}
