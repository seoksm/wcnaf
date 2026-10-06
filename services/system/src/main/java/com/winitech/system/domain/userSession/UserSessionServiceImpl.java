package com.winitech.system.domain.userSession;

import com.winitech.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * com.winitech.system.domain.userSession
 * └ UserSessionServiceImpl.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl extends EgovAbstractServiceImpl implements UserSessionService {

	private final UserSessionStore userSessionStore;
	private final UserSessionReader userSessionReader;

	@Value("${winitech.security.refresh-token.length:108}")
	private Short refreshTokenLength;

	@Value("${winitech.security.refresh-token.expires-in-seconds:2592000}")
	private Integer refreshTokenExpiresInSeconds;

	@Value("${winitech.security.refresh-token.rotation.access-token-refresh-cnt:0}")
	private Integer rotationByAccessTokenRefreshCnt;

	@Value("${winitech.security.refresh-token.rotation.threshold-in-seconds:0}")
	private Integer rotationByThresholdInSeconds;

	@Override
	public UserSessionInfo registerUserSession(UserSessionCommand userSessionCommand) {
		/*if (Boolean.TRUE.equals(userSessionReader.existLoginSessionBySessionIdAndUserId(userSessionCommand.getUserId(), UUID.randomUUID()))) {
			throw new IllegalStatusException("Already registered UserSession Name.");
		}*/
		UserSession initUserSession = UserSession.builder()
				.userId(userSessionCommand.getUserId())
				.loginAt(userSessionCommand.getLoginAt())
				.logoutAt(null)
				.loginIp(userSessionCommand.getLoginIp())
				.refreshToken(userSessionCommand.getRefreshToken())
				.refreshTokenExpiresAt(userSessionCommand.getRefreshTokenExpiresAt())
				.totalRefreshCnt(userSessionCommand.getTotalRefreshCnt())
				.refreshCnt(userSessionCommand.getRefreshCnt())
				.refreshTokenUpdatedAt(userSessionCommand.getRefreshTokenUpdatedAt())
				.loginStatus(userSessionCommand.getLoginStatus())
				.ipSecurityStatus(userSessionCommand.getIpSecurityStatus())
				.build();
		UserSession userSession = userSessionStore.store(initUserSession);
		return new UserSessionInfo(userSession);
	}

	/*@Override
	public UserSessionInfo modifyUserSession(UUID id, UserSessionCommand userSessionCommand) {
		UserSession modifyUserSession = userSessionReader.getUserSessionById(id);
		modifyUserSession.setName(userSessionCommand.getName());
		modifyUserSession.setState(cityReader.getCity(userSessionCommand.getCityId()).getState());
		modifyUserSession.setCity(cityReader.getCity(userSessionCommand.getCityId()));
		UserSession userSession = userSessionStore.store(modifyUserSession);
		return new UserSessionInfo(userSession);
	}*/

	public UserSessionInfo logout(UUID id, UserSessionCommand.Logout userSessionCommand) {
		UserSession modifyUserSession = userSessionReader.getUserSessionById(id);
		modifyUserSession.setLogoutAt(userSessionCommand.getLogoutAt());
		modifyUserSession.setRefreshToken(null);
		modifyUserSession.setLoginStatus(UserSession.LoginStatus.LOGOUT);
		UserSession userSession = userSessionStore.store(modifyUserSession);
		return new UserSessionInfo(userSession);
	}

	@Override
	public void removeUserSession(UUID id) {
		userSessionStore.remove(id);
	}

	@Override
	public Boolean existLoginSessionBySessionIdAndUserId(UUID sessionId, UUID userId) {
		return userSessionReader.existLoginSessionBySessionIdAndUserId(sessionId, userId);
	}

	@Override
	public UserSessionInfo getUserSessionById(UUID id) {
		return new UserSessionInfo(userSessionReader.getUserSessionById(id));
	}

	@Override
	public List<UserSessionInfo> getAllLoginSessionByUserId(UUID userId) {
		List<UserSession> loginSessions = userSessionReader.getAllLoginSessionByUserId(userId);
		
		return loginSessions.stream()
				.map(UserSessionInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<UserSessionInfo> getAllLoginSessionByUserId() {
		List<UserSession> loginSessions = userSessionReader.getAllLoginSession();
		
		return loginSessions.stream()
				.map(UserSessionInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public void logout(UUID userSessionId) {
		try {
			UserSession userSession = userSessionReader.getUserSessionById(userSessionId);
			userSession.setLoginStatus(UserSession.LoginStatus.LOGOUT);
			userSession.setLogoutAt(OffsetDateTime.now());
			userSessionStore.store(userSession);
		} catch (EntityNotFoundException ignored) {
			// 이미 로그아웃 되었거나 로그인 기록이 없으면 무시
		}
	}

	@Override
	public UserSessionInfo getUserSessionByRefreshToken(String refreshToken) {
		List<UserSession> userSessionList = userSessionReader.getAllUserSessionByRefreshToken(refreshToken);
		
		if (userSessionList.isEmpty()) {
			return null;
		}
		
		return new UserSessionInfo(userSessionList.get(0));
	}
	
	public boolean increaseRefreshCnt(UUID userSessionId) {
		UserSession userSession = userSessionReader.getUserSessionById(userSessionId);
		userSession.setRefreshCnt(userSession.getRefreshCnt() + 1);
		userSession.setTotalRefreshCnt(userSession.getTotalRefreshCnt() + 1);
		userSessionStore.store(userSession);
		
		return true;
	}

	/**
	 * access token 재사용 횟수와 refresh token 재발급 제한 시간에 따라 필요시 RefreshToken을 재발급합니다.
	 * 재발급할 필요가 없을 경우 기존 UserSession을 반환합니다.
	 * @param userSessionId 사용자 세션 ID
	 * @return
	 */
	@Override
	@Transactional
	public UserSessionInfo rotateRefreshTokenIfNeeded(UUID userSessionId) {
		return rotateRefreshTokenIfNeeded(userSessionId, rotationByAccessTokenRefreshCnt, rotationByThresholdInSeconds);
	}
	
	/**
	 * access token 재사용 횟수와 refresh token 재발급 제한 시간에 따라 필요시 RefreshToken을 재발급합니다.
	 * 재발급할 필요가 없을 경우 기존 UserSession을 반환합니다.
	 * @param userSessionId 사용자 세션 ID
	 * @param rotationByAccessTokenRefreshCnt access token 재사용 횟수
	 * @param rotationByThresholdInSeconds refresh token 재발급 제한 시간 (refresh token 발급 시간(refreshTokenUpdatedAt)에서 어느정도 지나면 rotation 할 지 지정)
	 * @return
	 */
	@Override
	@Transactional
	public UserSessionInfo rotateRefreshTokenIfNeeded(UUID userSessionId, Integer rotationByAccessTokenRefreshCnt, Integer rotationByThresholdInSeconds) {
		OffsetDateTime now = OffsetDateTime.now();

		UserSession userSession = userSessionReader.getUserSessionById(userSessionId);

		OffsetDateTime refreshTokenUpdatedAt = userSession.getRefreshTokenUpdatedAt();
		Integer refreshCnt = userSession.getRefreshCnt();
			
		boolean isNeedToRotate = checkRefreshTokenNeedToRotate(rotationByAccessTokenRefreshCnt, rotationByThresholdInSeconds, refreshCnt, refreshTokenUpdatedAt, now);

		if (isNeedToRotate)	{
			String newRefreshToken = generateRefreshToken(userSession.getUserId());
			userSession.setRefreshToken(newRefreshToken);
			userSession.setRefreshTokenUpdatedAt(now);

			// 재발급되었으므로 access token refresh 횟수 초기화
			userSession.setRefreshCnt(0);

			userSessionStore.store(userSession);
		}
		
		return new UserSessionInfo(userSession);
	}

	/**
	 * refreshToken을 재발급해야 하는지 확인합니다.
	 * @param rotationByAccessTokenRefreshCnt access token 재사용 횟수 (application.properties 설정 )
	 * @param rotationByThresholdInSeconds refresh token 재발급 제한 시간 (application.properties 설정 )
	 * @param curRefreshCnt 현재 refresh 횟수
	 * @param refreshTokenUpdatedAt refreshToken 갱신 시간
	 * @param now 현재 시간
	 * @return 재발급이 필요하면 true, 아니면 false
	 */
	private static boolean checkRefreshTokenNeedToRotate(Integer rotationByAccessTokenRefreshCnt, Integer rotationByThresholdInSeconds, Integer curRefreshCnt, OffsetDateTime refreshTokenUpdatedAt, OffsetDateTime now) {
		boolean isNeedToRotate = false;
		boolean isRefreshCntLimitSet = rotationByAccessTokenRefreshCnt != null && rotationByAccessTokenRefreshCnt > 0;
		boolean isThresholdInSecondsLimitSet = rotationByThresholdInSeconds != null && rotationByThresholdInSeconds > 0;

		if (isThresholdInSecondsLimitSet && (refreshTokenUpdatedAt == null || refreshTokenUpdatedAt.plusSeconds(rotationByThresholdInSeconds).isBefore(now))) {
			// refreshTokenUpdatedAt가 없거나 rotationByThresholdInSeconds 시간이 지났으면 refreshToken을 재발급
			isNeedToRotate = true;
		} else if (isRefreshCntLimitSet && (curRefreshCnt != null && curRefreshCnt >= rotationByAccessTokenRefreshCnt)) {
			// refreshCnt가 rotationByAccessTokenRefreshCnt보다 크거나 같으면 refreshToken을 재발급
			isNeedToRotate = true;
		}
		return isNeedToRotate;
	}

	@Override
	public String generateRefreshToken(UUID userId) {
		String uuid = userId.toString().replace("-", "");
		
		return uuid + RandomStringUtils.randomAlphanumeric(refreshTokenLength - uuid.length());
	}

	@Override
	public UserSessionInfo getLastLoginUserSessionByUserId(UUID userId) {
		try {
			UserSession lastLoginUserSessionByUserId = userSessionReader.getLastLoginUserSessionByUserId(userId);
			return new UserSessionInfo(lastLoginUserSessionByUserId);
		} catch (EntityNotFoundException _ignored) {
			// 로그인 기록이 없으면 null 반환
			return null;
		}
	}
}
