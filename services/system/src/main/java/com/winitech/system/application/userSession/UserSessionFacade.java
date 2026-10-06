package com.winitech.system.application.userSession;

import com.winitech.system.domain.userSession.UserSession;
import com.winitech.system.domain.userSession.UserSessionCommand;
import com.winitech.system.domain.userSession.UserSessionInfo;
import com.winitech.system.domain.userSession.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.application.userSession
 * └ UserSessionFacade.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionFacade {
	private final UserSessionService userSessionService;

	public UserSessionInfo registerUserSession(UserSessionCommand userSessionCommand) {
		return userSessionService.registerUserSession(userSessionCommand);
	}

	public void removeUserSession(UUID id) {
		userSessionService.removeUserSession(id);
	}

	public Boolean existLoginSessionBySessionIdAndUserId(UUID sessionId, UUID userId) {
		return userSessionService.existLoginSessionBySessionIdAndUserId(sessionId, userId);
	}

	public UserSessionInfo getUserSessionById(UUID id) {
		return userSessionService.getUserSessionById(id);
	}

	public List<UserSessionInfo> getAllLoginSessionByUserId(UUID userId) {
		return userSessionService.getAllLoginSessionByUserId(userId);
	}

	public List<UserSessionInfo> getAllLoginSessionByUserId() {
		return userSessionService.getAllLoginSessionByUserId();
	}

	public UserSessionInfo getUserSessionByRefreshToken(String refreshToken) {
		return userSessionService.getUserSessionByRefreshToken(refreshToken);
	}

	public void setRefreshTokenCookie(HttpServletRequest req, HttpServletResponse res, String refreshToken, Integer refreshTokenExpiresIn) {
		Cookie token = new Cookie("refreshToken", refreshToken);
		token.setPath("/api/v1/system/user/refreshToken");
		token.setHttpOnly(true);

		if ("https".equals(req.getScheme())) {
			token.setSecure(true);
		}

		token.setMaxAge(refreshTokenExpiresIn);
		res.addCookie(token);
	}
}
