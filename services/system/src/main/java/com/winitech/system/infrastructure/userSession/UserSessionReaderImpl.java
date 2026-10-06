package com.winitech.system.infrastructure.userSession;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.system.domain.userSession.UserSession;
import com.winitech.system.domain.userSession.UserSessionReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.infrastructure.userSession
 * └ UserSessionReaderImpl.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserSessionReaderImpl implements UserSessionReader {
	private final UserSessionRepository userSessionRepository;

	@Override
	public List<UserSession> getAllLoginSession() {
		return userSessionRepository.findByLoginStatus(UserSession.LoginStatus.LOGIN).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<UserSession> getAllLoginSessionByUserId(UUID userId) {
		return userSessionRepository.findByUserIdAndLoginStatus(userId, UserSession.LoginStatus.LOGIN).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public Boolean existLoginSessionBySessionIdAndUserId(UUID sessionId, UUID userId) {
		return userSessionRepository.existsByIdAndUserIdAndLoginStatus(sessionId, userId, UserSession.LoginStatus.LOGIN);
	}

	@Override
	public UserSession getUserSessionById(UUID userSessionId) {
		return userSessionRepository.findByIdAndLoginStatus(userSessionId, UserSession.LoginStatus.LOGIN).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<UserSession> getAllUserSessionByRefreshToken(String refreshToken) {
		return userSessionRepository.findAllByRefreshTokenAndRefreshTokenExpiresAtGreaterThanOrderByRefreshTokenExpiresAtDesc(refreshToken, OffsetDateTime.now());
	}

	@Override
	public UserSession getLastLoginUserSessionByUserId(UUID userId) {
		return userSessionRepository.findFirstByUserIdAndLoginStatusOrderByLoginAtDesc(userId, UserSession.LoginStatus.LOGIN).orElseThrow(EntityNotFoundException::new);
	}
}
