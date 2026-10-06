package com.winitech.system.domain.userSession;

import java.util.UUID;

/**
 * com.winitech.system.domain.userSession
 * └ UserSessionStore.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
public interface UserSessionStore {
	UserSession store(UserSession userSession);

	UserSession modify(UserSession userSession, UserSessionCommand command);
	
	void remove(UUID userSessionId);
}
