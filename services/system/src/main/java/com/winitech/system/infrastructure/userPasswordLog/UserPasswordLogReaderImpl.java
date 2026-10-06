package com.winitech.system.infrastructure.userPasswordLog;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.system.domain.userPasswordLog.UserPasswordLog;
import com.winitech.system.domain.userPasswordLog.UserPasswordLogReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.userPasswordLog
 * └ UserPasswordLogReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 13:37
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPasswordLogReaderImpl implements UserPasswordLogReader {
	private final UserPasswordLogRepository userPasswordLogRepository;

	@Override
	public UserPasswordLog getUserPasswordLogById(UUID id) {
		return userPasswordLogRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<UserPasswordLog> getLastNthUserPasswordLogByUserId(UUID userId, int userPasswordLogCount) {
		UserPasswordLog probe = new UserPasswordLog();
		probe.setUserId(userId);

		PageRequest pageable = PageRequest.of(0, userPasswordLogCount, Sort.by(Sort.Order.desc("id")));
		return userPasswordLogRepository.findAll(Example.of(probe), pageable).getContent();
	}
}
