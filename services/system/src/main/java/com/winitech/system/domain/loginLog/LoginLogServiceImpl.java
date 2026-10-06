package com.winitech.system.domain.loginLog;

import com.winitech.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.loginLog
 * └ LoginLogServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-20 17:51
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl extends EgovAbstractServiceImpl implements LoginLogService {

	private final LoginLogStore loginLogStore;
	private final LoginLogReader loginLogReader;

	@Override
	public LoginLogInfo registerLoginLog(LoginLogCommand loginLogCommand) {
		LoginLog initLoginLog = LoginLog.builder()
				.username(loginLogCommand.getUsername())
				.loginIp(loginLogCommand.getLoginIp())
				.loginLogStatus(loginLogCommand.getLoginLogStatus())
				.userSessionId(loginLogCommand.getUserSessionId())
				.errCnt(loginLogCommand.getErrCnt())
				.lockUntil(loginLogCommand.getLockUntil())
				.build();
		LoginLog loginLog = loginLogStore.store(initLoginLog);
		return new LoginLogInfo(loginLog);
	}

	@Override
	public LoginLogInfo modifyLoginLog(UUID id, LoginLogCommand loginLogCommand) {
		LoginLog modifyLoginLog = loginLogReader.getLoginLogById(id);

		modifyLoginLog.setLoginLogStatus(loginLogCommand.getLoginLogStatus());
		modifyLoginLog.setUserSessionId(loginLogCommand.getUserSessionId());
		modifyLoginLog.setErrCnt(loginLogCommand.getErrCnt());
		modifyLoginLog.setLockUntil(loginLogCommand.getLockUntil());

		LoginLog loginLog = loginLogStore.store(modifyLoginLog);
		return new LoginLogInfo(loginLog);
	}

	@Override
	public void removeLoginLog(UUID id) {
		loginLogStore.removeLoginLog(id);
	}

	@Override
	public LoginLogInfo searchLoginLogById(UUID id) {
		LoginLog loginLog = loginLogReader.getLoginLogById(id);
		return new LoginLogInfo(loginLog);
	}

	@Override
	public LoginLogInfo searchLastLoginLogByUsernameAndIp(String username, String loginIp) {
		try {
			LoginLog loginLog = loginLogReader.getLastLoginLogByUsernameAndIp(username, loginIp);
			return new LoginLogInfo(loginLog);
		} catch (EntityNotFoundException _ignored) {
			// Entity가 없을 경우 null을 리턴
			return null;
		}
	}

	@Override
	public void unlockLogin(String username) {
		loginLogStore.unlockLoginByUsernameAndIp(username, null);
	}

	@Override
	public void unlockLogin(String username, String loginIp) {
		loginLogStore.unlockLoginByUsernameAndIp(username, loginIp);
	}
}
