package com.winitech.system.domain.loginLog;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.loginLog
 * └ LoginLogService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-20 17:51
 **/
public interface LoginLogService {
	LoginLogInfo registerLoginLog(LoginLogCommand loginLogCommand);

	LoginLogInfo modifyLoginLog(UUID id, LoginLogCommand loginLogCommand);

	void removeLoginLog(UUID id);

	LoginLogInfo searchLoginLogById(UUID id);

	LoginLogInfo searchLastLoginLogByUsernameAndIp(String username, String loginIp);

	void unlockLogin(String username);
	
	void unlockLogin(String username, String loginIp);
}
