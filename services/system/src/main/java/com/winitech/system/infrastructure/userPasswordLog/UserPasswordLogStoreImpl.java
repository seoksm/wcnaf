package com.winitech.system.infrastructure.userPasswordLog;

import com.winitech.system.domain.userPasswordLog.UserPasswordLog;
import com.winitech.system.domain.userPasswordLog.UserPasswordLogCommand;
import com.winitech.system.domain.userPasswordLog.UserPasswordLogStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.userPasswordLog
 * └ UserPasswordLogStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 13:37
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPasswordLogStoreImpl implements UserPasswordLogStore {
	private final UserPasswordLogRepository userPasswordLogRepository;

	@Override
	public UserPasswordLog store(UserPasswordLog userPasswordLog) {
		return userPasswordLogRepository.save(userPasswordLog);
	}

	@Override
	public UserPasswordLog modify(UserPasswordLog userPasswordLog, UserPasswordLogCommand command) {
		userPasswordLog.setUserId(command.getUserId());
		userPasswordLog.setHashedPassword(command.getHashedPassword());
		userPasswordLog.setPasswordChangedAt(command.getPasswordChangedAt());
		userPasswordLog.setPasswordChangeIp(command.getPasswordChangeIp());
		return userPasswordLogRepository.save(userPasswordLog);
	}

	@Override
	public void remove(UUID userPasswordLogId) {
		userPasswordLogRepository.deleteById(userPasswordLogId);
	}

	@Override
	public void removeNotInIdList(UUID userId, List<UUID> userPasswordLogIdList) {
		userPasswordLogRepository.deleteByUserIdAndIdNotIn(userId, userPasswordLogIdList);
	}
}
