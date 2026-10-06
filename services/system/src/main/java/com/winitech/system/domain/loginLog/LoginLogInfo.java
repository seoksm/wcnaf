package com.winitech.system.domain.loginLog;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.loginLog
 * └ LoginLogInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-20 17:51
 **/
@Getter
public class LoginLogInfo {
	private final UUID id;
	private final String username;
	private final String loginIp;
	private final LoginLog.LoginLogStatus loginLogStatus;
	private final UUID userSessionId;
	private final Integer errCnt;
	private final OffsetDateTime lockUntil;

	public LoginLogInfo(LoginLog loginLog) {
		this.id = loginLog.getId();
		this.username = loginLog.getUsername();
		this.loginIp = loginLog.getLoginIp();
		this.loginLogStatus = loginLog.getLoginLogStatus();
		this.userSessionId = loginLog.getUserSessionId();
		this.errCnt = loginLog.getErrCnt();
		this.lockUntil = loginLog.getLockUntil();
	}
}
