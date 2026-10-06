package com.winitech.system.domain.loginLog;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.loginLog
 * └ LoginLogReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-20 17:51
 **/
public interface LoginLogReader {
	LoginLog getLoginLogById(UUID id);

	LoginLog getLastLoginLogByUsernameAndIp(String username, String loginIp);

	boolean existsTodayLoginLogByUsername(String username);
}