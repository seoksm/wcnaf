package com.winitech.system.domain.userSession;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * com.winitech.system.domain.userSession
 * └ UserSessionInfo.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
@Getter
public class UserSessionInfo {
	private final UUID id;
	private final UUID userId;
	private final OffsetDateTime loginAt;
	private final OffsetDateTime logoutAt;
	private final String loginIp;
	
	private final String refreshToken;
	private final OffsetDateTime refreshTokenExpiresAt;
	private final Integer totalRefreshCnt;
	private final Integer refreshCnt;
	private final OffsetDateTime refreshTokenUpdatedAt;
	
	private final UserSession.LoginStatus loginStatus;
	private final UserSession.IpSecurityStatus ipSecurityStatus;

	public UserSessionInfo(UserSession userSession) {
		this.id = userSession.getId();
		this.userId = userSession.getUserId();
		this.loginAt = userSession.getLoginAt();
		this.logoutAt = userSession.getLogoutAt();
		this.loginIp = userSession.getLoginIp();
		
		this.refreshToken = userSession.getRefreshToken();
		this.refreshTokenExpiresAt = userSession.getRefreshTokenExpiresAt();
		this.totalRefreshCnt = userSession.getTotalRefreshCnt();
		this.refreshCnt = userSession.getRefreshCnt();
		this.refreshTokenUpdatedAt = userSession.getRefreshTokenUpdatedAt();
		
		this.loginStatus = userSession.getLoginStatus();
		this.ipSecurityStatus = userSession.getIpSecurityStatus();
	}
}
