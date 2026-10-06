package com.winitech.system.domain.userPasswordLog;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.userPasswordLog
 * └ UserPasswordLogInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 13:36
 **/
@Getter
public class UserPasswordLogInfo {
	private final UUID id;
	private final UUID userId;
	private final String hashedPassword;
	private final OffsetDateTime passwordChangedAt;
	private final String passwordChangeIp;

	public UserPasswordLogInfo(UserPasswordLog userPasswordLog) {
		this.id = userPasswordLog.getId();
		this.userId = userPasswordLog.getUserId();
		this.hashedPassword = userPasswordLog.getHashedPassword();
		this.passwordChangedAt = userPasswordLog.getPasswordChangedAt();
		this.passwordChangeIp = userPasswordLog.getPasswordChangeIp();				
	}
}
