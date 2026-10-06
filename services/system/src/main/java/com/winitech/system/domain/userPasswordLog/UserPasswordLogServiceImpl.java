package com.winitech.system.domain.userPasswordLog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.userPasswordLog
 * └ UserPasswordLogServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 13:36
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPasswordLogServiceImpl extends EgovAbstractServiceImpl implements UserPasswordLogService {
	private final UserPasswordLogStore userPasswordLogStore;
	private final UserPasswordLogReader userPasswordLogReader;

	@Override
	public UserPasswordLogInfo registerUserPasswordLog(UserPasswordLogCommand userPasswordLogCommand) {
		UserPasswordLog initUserPasswordLog = UserPasswordLog.builder()
				.userId(userPasswordLogCommand.getUserId())
				.hashedPassword(userPasswordLogCommand.getHashedPassword())
				.passwordChangedAt(userPasswordLogCommand.getPasswordChangedAt())
				.passwordChangeIp(userPasswordLogCommand.getPasswordChangeIp())
				.build();
		UserPasswordLog userPasswordLog = userPasswordLogStore.store(initUserPasswordLog);
		return new UserPasswordLogInfo(userPasswordLog);
	}

	@Override
	public UserPasswordLogInfo modifyUserPasswordLog(UUID id, UserPasswordLogCommand userPasswordLogCommand) {
		UserPasswordLog modifyUserPasswordLog = userPasswordLogReader.getUserPasswordLogById(id);
		modifyUserPasswordLog.setUserId(userPasswordLogCommand.getUserId());
		modifyUserPasswordLog.setHashedPassword(userPasswordLogCommand.getHashedPassword());
		modifyUserPasswordLog.setPasswordChangedAt(userPasswordLogCommand.getPasswordChangedAt());
		modifyUserPasswordLog.setPasswordChangeIp(userPasswordLogCommand.getPasswordChangeIp());
		UserPasswordLog userPasswordLog = userPasswordLogStore.store(modifyUserPasswordLog);
		return new UserPasswordLogInfo(userPasswordLog);
	}

	@Override
	public void removeUserPasswordLog(UUID id) {
		userPasswordLogStore.remove(id);
	}

	@Override
	public UserPasswordLogInfo searchUserPasswordLogById(UUID id) {
		UserPasswordLog userPasswordLog = userPasswordLogReader.getUserPasswordLogById(id);
		return new UserPasswordLogInfo(userPasswordLog);
	}

	@Override
	public List<UserPasswordLog> searchLastNthUserPasswordLogByUserId(UUID userId, int userPasswordLogCount) {
		return userPasswordLogReader.getLastNthUserPasswordLogByUserId(userId, userPasswordLogCount);
	}

	@Override
	public void cleanUpUserPasswordLog(UUID userId, int keepCount) {
		List<UserPasswordLog> userPasswordLogs = userPasswordLogReader.getLastNthUserPasswordLogByUserId(userId, keepCount);
		List<UUID> userPasswordLogIds = userPasswordLogs.stream().map(UserPasswordLog::getId).collect(Collectors.toList());
		
		userPasswordLogStore.removeNotInIdList(userId, userPasswordLogIds);
	}
}
