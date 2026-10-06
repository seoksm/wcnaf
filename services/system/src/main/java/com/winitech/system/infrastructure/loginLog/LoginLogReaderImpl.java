package com.winitech.system.infrastructure.loginLog;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.system.domain.loginLog.LoginLog;
import com.winitech.system.domain.loginLog.LoginLogReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.loginLog
 * └ LoginLogReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-20 18:05
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginLogReaderImpl implements LoginLogReader {
	private final LoginLogRepository loginLogRepository;

	@Override
	public LoginLog getLastLoginLogByUsernameAndIp(String username, String loginIp) {
		return loginLogRepository.findFirstByUsernameAndLoginIpOrderByIdDesc(username, loginIp).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public LoginLog getLoginLogById(UUID id) {
		return loginLogRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public boolean existsTodayLoginLogByUsername(String username) {

		OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
		OffsetDateTime startOfDay = now.toLocalDate().atStartOfDay().atZone(ZoneId.of("Asia/Seoul")).withZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();
		OffsetDateTime endOfDay = startOfDay.plusDays(1);

		return loginLogRepository.existsByUsernameAndLoginLogStatusAndCreateAtBetween(username, LoginLog.LoginLogStatus.LOGIN, startOfDay, endOfDay);
	}
}
