package com.winitech.system.infrastructure.authorizationGroupUser;

import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUser;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserCommand;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserReader;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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
public class AuthorizationGroupUserStoreImpl implements AuthorizationGroupUserStore {
	private final AuthorizationGroupUserRepository authorizationGroupUserRepository;
	private final AuthorizationGroupUserReader authorizationGroupUserReader;

	@Override
	public AuthorizationGroupUser store(AuthorizationGroupUser authorizationGroupUser) {
		return authorizationGroupUserRepository.save(authorizationGroupUser);
	}

	@Override
	public List<AuthorizationGroupUser> storeAll(List<AuthorizationGroupUser> toSaveList) {
		return authorizationGroupUserRepository.saveAll(toSaveList);
	}

	@Override
	public void remove(UUID authorizationGroupUserId) {
		authorizationGroupUserRepository.deleteById(authorizationGroupUserId);
	}
	
	@Override
	public void remove(UUID authorizationGroupId, UUID userId) {
		authorizationGroupUserRepository.deleteByAuthorizationGroupIdAndUserId(authorizationGroupId, userId);
	}
}
