package com.winitech.system.domain.loginLog;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.loginLog
 * └ LoginLogStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-20 17:51
 **/
public interface LoginLogStore {
	LoginLog store(LoginLog loginLog);

	LoginLog modify(LoginLog loginLog, LoginLogCommand command);

	void removeLoginLog(UUID loginLogId);

	void unlockLoginByUsernameAndIp(String username, String loginIp);
}
