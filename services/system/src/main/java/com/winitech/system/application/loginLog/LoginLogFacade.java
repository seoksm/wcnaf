package com.winitech.system.application.loginLog;

import com.winitech.common.exception.UnauthenticatedException;
import com.winitech.system.domain.loginLog.LoginLog;
import com.winitech.system.domain.loginLog.LoginLogCommand;
import com.winitech.system.domain.loginLog.LoginLogInfo;
import com.winitech.system.domain.loginLog.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.application.loginLog
 * └ LoginLogFacade.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 09:15
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogFacade {
	private final LoginLogService loginLogService;
	
	@Value("${winitech.security.login.lock-count:0}")
	private Integer loginLockCount;

	@Value("${winitech.security.login.lock-duration-in-seconds:60}")
	private Long loginLockDuration;

	@Value("${winitech.security.login.password.expires-in-days:365}")
	private Integer passwordExpiresInDays;

	public LoginLogInfo registerLoginLog(LoginLogCommand loginLogCommand) {
		return loginLogService.registerLoginLog(loginLogCommand);
	}

	public LoginLogInfo modifyLoginLog(UUID id, LoginLogCommand loginLogCommand) {
		return loginLogService.modifyLoginLog(id, loginLogCommand);
	}

	public void removeLoginLog(UUID id) {
		loginLogService.removeLoginLog(id);
	}

	public LoginLogInfo searchLoginLogById(UUID id) {
		return loginLogService.searchLoginLogById(id);
	}

	public void checkUsernameAndIpIsLocked(String username, String loginIp) {
		LoginLogInfo loginLogInfo = loginLogService.searchLastLoginLogByUsernameAndIp(username, loginIp);

		if (loginLogInfo == null || loginLogInfo.getLockUntil() == null || loginLogInfo.getLoginLogStatus() != LoginLog.LoginLogStatus.FAILED) {
			// 이전의 실패 기록이 없으면 정상 종료
			return;
		}
		
		long lockDuration = loginLogInfo.getLockUntil().toEpochSecond() - OffsetDateTime.now().toEpochSecond();

		if (lockDuration > 0) {
			// 계정이 잠겨있으면 예외 발생
			
			throw new UnauthenticatedException(String.format("Your account is locked. Please try again in %d seconds.", lockDuration));
		}
	}

	public void addLoginSuccessLog(String username, String clientIp, UUID userSessionId) {
		LoginLogCommand loginLogCommand = LoginLogCommand.builder()
				.username(username)
				.loginIp(clientIp)
				.loginLogStatus(LoginLog.LoginLogStatus.LOGIN)
				.errCnt(0)
				.lockUntil(null)
				.userSessionId(userSessionId)
				.build();

		loginLogService.registerLoginLog(loginLogCommand);
	}
	
	public void addLoginFailLog(String username, String clientIp) {
		LoginLogInfo loginLogInfo = loginLogService.searchLastLoginLogByUsernameAndIp(username, clientIp);
		
		int nextErrCnt;
		OffsetDateTime nextLockUntil;

		if (loginLogInfo == null || loginLogInfo.getLoginLogStatus() == LoginLog.LoginLogStatus.LOGIN || loginLogInfo.getLoginLogStatus() == LoginLog.LoginLogStatus.UNLOCK) {
			nextErrCnt = 1;
		} else {
			nextErrCnt = loginLogInfo.getErrCnt() + 1;
		}
		
		if (loginLockCount > 0 && nextErrCnt >= loginLockCount) {
			nextLockUntil = OffsetDateTime.now().plusSeconds(loginLockDuration);
		} else {
			nextLockUntil = null;
		}

		LoginLogCommand loginLogCommand = LoginLogCommand.builder()
				.username(username)
				.loginIp(clientIp)
				.loginLogStatus(LoginLog.LoginLogStatus.FAILED)
				.errCnt(nextErrCnt)
				.lockUntil(nextLockUntil)
				.build();

		if (loginLogInfo == null || loginLogInfo.getLoginLogStatus() == LoginLog.LoginLogStatus.LOGIN) {
			loginLogService.registerLoginLog(loginLogCommand);
		} else {
			loginLogService.modifyLoginLog(loginLogInfo.getId(), loginLogCommand);
		}
	}

	public void unlockLogin(String username) {
		loginLogService.unlockLogin(username, null);
	}

	public void unlockLogin(String username, String loginIp) {
		loginLogService.unlockLogin(username, loginIp);
	}
}
