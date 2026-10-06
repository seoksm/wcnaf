package com.winitech.system.infrastructure.userSession;

import com.winitech.system.domain.userSession.UserSession;
import com.winitech.system.domain.userSession.UserSessionCommand;
import com.winitech.system.domain.userSession.UserSessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * com.winitech.system.infrastructure.userSession
 * └ UserSessionStoreImpl.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserSessionStoreImpl implements UserSessionStore {
	private final UserSessionRepository userSessionRepository;

	@Override
	public UserSession store(UserSession userSession) {
		return userSessionRepository.save(userSession);
	}

	@Override
	public UserSession modify(UserSession userSession, UserSessionCommand command) {
		userSession.setId(command.getId());
		userSession.setUserId(command.getUserId());
		userSession.setLoginAt(command.getLoginAt());
		userSession.setLogoutAt(command.getLogoutAt());
		userSession.setLoginIp(command.getLoginIp());
		userSession.setRefreshToken(command.getRefreshToken());
		userSession.setLoginStatus(command.getLoginStatus());
		return userSessionRepository.save(userSession);
	}

	@Override
	public void remove(UUID userSessionId) {
		userSessionRepository.deleteById(userSessionId);
	}
}
