package com.winitech.system.infrastructure.loginLog;

import com.winitech.system.domain.loginLog.LoginLog;
import com.winitech.system.domain.loginLog.LoginLogCommand;
import com.winitech.system.domain.loginLog.LoginLogStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.infrastructure.loginLog
 * └ LoginLogStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-20 18:06
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginLogStoreImpl implements LoginLogStore {
	private final LoginLogRepository loginLogRepository;

	@Override
	public LoginLog store(LoginLog loginLog) {
		return loginLogRepository.save(loginLog);
	}

	@Override
	public LoginLog modify(LoginLog loginLog, LoginLogCommand command) {
		loginLog.setErrCnt(command.getErrCnt());
		loginLog.setLockUntil(command.getLockUntil());
		
		return loginLogRepository.save(loginLog);
	}

	@Override
	public void removeLoginLog(UUID loginLogId) {
		loginLogRepository.deleteById(loginLogId);
	}

	@Override
	public void unlockLoginByUsernameAndIp(String username, String loginIp) {
		OffsetDateTime now = OffsetDateTime.now();

		List<LoginLog> lockedLoginLogList = loginLogRepository.findByUsernameAndLoginLogStatusAndLockUntilGreaterThan(username, LoginLog.LoginLogStatus.FAILED, now);
		
		if (loginIp != null) {
			// IP 필터는 쿼리에서 하지 않고 별도로 수행
			lockedLoginLogList = lockedLoginLogList.stream()
					.filter(loginLog -> loginIp.equals(loginLog.getLoginIp()))
					.collect(Collectors.toList());
		}
		
		lockedLoginLogList.forEach(loginLog -> {
			loginLog.setLoginLogStatus(LoginLog.LoginLogStatus.UNLOCK);
		});
		
		loginLogRepository.saveAll(lockedLoginLogList);
	}
}
