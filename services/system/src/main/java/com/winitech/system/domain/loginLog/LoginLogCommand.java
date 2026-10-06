package com.winitech.system.domain.loginLog;

import com.winitech.system.domain.userSession.UserSession;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.loginLog
 * └ LoginLogCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-20 17:50
 **/
@Getter
@Builder
@ToString
public class LoginLogCommand {
	private final String username;
	private final String loginIp;
	private final LoginLog.LoginLogStatus loginLogStatus;
	private final UUID userSessionId;
	private final Integer errCnt;
	private final OffsetDateTime lockUntil;

	public LoginLog toEntity() {
		return LoginLog.builder()
				.username(username)
				.loginIp(loginIp)
				.loginLogStatus(loginLogStatus)
				.userSessionId(userSessionId)
				.errCnt(errCnt)
				.lockUntil(lockUntil)
				.build();
	}
}
