package com.winitech.system.domain.userSession;

import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.domain.userSession
 * └ UserSessionReader.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
public interface UserSessionReader {
	UserSession getUserSessionById(UUID userSessionId);

	Boolean existLoginSessionBySessionIdAndUserId(UUID userSessionId, UUID userId);

	List<UserSession> getAllLoginSessionByUserId(UUID userId);

	List<UserSession> getAllLoginSession();

	List<UserSession> getAllUserSessionByRefreshToken(String refreshToken);

	UserSession getLastLoginUserSessionByUserId(UUID userId);
}