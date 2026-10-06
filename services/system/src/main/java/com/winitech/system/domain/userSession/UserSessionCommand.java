package com.winitech.system.domain.userSession;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * com.winitech.system.domain.userSession
 * └ UserSessionCommand.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
@Getter
@Builder
@ToString
public class UserSessionCommand {
	private final UUID id;
	private final UUID userId;
	private final OffsetDateTime loginAt;
	private final OffsetDateTime logoutAt;
	private final String loginIp;
	private final String logoutIp;
	private final String refreshToken;
	private final OffsetDateTime refreshTokenExpiresAt;
	private final Integer totalRefreshCnt;
	private final Integer refreshCnt;
	private final OffsetDateTime refreshTokenUpdatedAt;
	private final UserSession.LoginStatus loginStatus;
	private final UserSession.IpSecurityStatus ipSecurityStatus;

	public UserSession toEntity() {
		return UserSession.builder()
				.id(id)
				.userId(userId)
				.loginAt(loginAt)
				.logoutAt(logoutAt)
				.loginIp(loginIp)
				.refreshToken(refreshToken)
				.refreshTokenExpiresAt(refreshTokenExpiresAt)
				.totalRefreshCnt(totalRefreshCnt)
				.refreshCnt(refreshCnt)
				.refreshTokenUpdatedAt(refreshTokenUpdatedAt)
				.loginStatus(loginStatus)
				.ipSecurityStatus(ipSecurityStatus)
				.build();
	}

	@Getter
	@Builder
	@ToString
	public static class Logout {
		private final OffsetDateTime logoutAt;
		private final String logoutIp;
	}
}
