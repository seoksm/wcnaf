package com.winitech.system.domain.userPasswordLog;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.userPasswordLog
 * └ UserPasswordLogCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 13:36
 **/
@Getter
@Builder
@ToString
public class UserPasswordLogCommand {
	private final UUID userId;
	private final String hashedPassword;
	private final OffsetDateTime passwordChangedAt;
	private final String passwordChangeIp;

	public UserPasswordLog toEntity() {
		return UserPasswordLog.builder()
				.userId(userId)
				.hashedPassword(hashedPassword)
				.passwordChangedAt(passwordChangedAt)
				.passwordChangeIp(passwordChangeIp)
				.build();
	}
}
