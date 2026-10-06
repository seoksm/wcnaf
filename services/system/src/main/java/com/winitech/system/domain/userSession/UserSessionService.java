package com.winitech.system.domain.userSession;

import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.domain.userSession
 * └ UserSessionService.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
public interface UserSessionService {
	UserSessionInfo registerUserSession(UserSessionCommand userSessionCommand);

	// UserSessionInfo modifyUserSession(UUID id, UserSessionCommand userSessionCommand);

	void removeUserSession(UUID id);

	Boolean existLoginSessionBySessionIdAndUserId(UUID sessionId, UUID userId);

	UserSessionInfo getUserSessionById(UUID id);

	List<UserSessionInfo> getAllLoginSessionByUserId(UUID userId);

	List<UserSessionInfo> getAllLoginSessionByUserId();

	void logout(UUID userSessionId);

	UserSessionInfo getUserSessionByRefreshToken(String refreshToken);

	boolean increaseRefreshCnt(UUID userSessionId);
	
	UserSessionInfo rotateRefreshTokenIfNeeded(UUID userSessionId);
	
	UserSessionInfo rotateRefreshTokenIfNeeded(UUID userSessionId, Integer rotationByAccessTokenRefreshCnt, Integer rotationByThresholdInSeconds);
	
	String generateRefreshToken(UUID userId);

	UserSessionInfo getLastLoginUserSessionByUserId(UUID id);
}
