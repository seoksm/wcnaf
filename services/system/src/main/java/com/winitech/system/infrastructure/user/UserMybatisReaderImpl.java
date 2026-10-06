package com.winitech.system.infrastructure.user;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserMybatisReader;
import com.winitech.system.domain.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.inject.Qualifier;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.user
 * └ UserMybatisReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-06-16 15:08
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserMybatisReaderImpl implements UserMybatisReader {
	private final UserMapper userMapper;

	@Override
	public User getUser(UUID id) {
		return userMapper.findByIdAndStatus(id, User.Status.ENABLE)
				.orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<User> getUserListById(List<UUID> idList) {
		return userMapper.findByIdInAndStatus(idList, User.Status.ENABLE);
	}

	@Override
	public User getUserByUsername(String username) {
		return userMapper.findByUsernameAndStatus(username, User.Status.ENABLE)
				.orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<User> getAllUser() {
		return userMapper.findAllByStatus(User.Status.ENABLE);
	}

	@Override
	public List<User> getAllUser(List<UUID> userIds) {
		return userMapper.findAllByStatusAndIdIn(User.Status.ENABLE, userIds);
	}

	@Override
	public List<User> getAllJoinedUser() {
		return userMapper.findAllByStatusAndJoinStatus(User.Status.ENABLE, User.JoinStatus.ACCEPTED);
	}

	@Override
	public List<User> getAllUserByJoinStatus(User.JoinStatus joinStatus) {
		return userMapper.findAllByStatusAndJoinStatus(User.Status.ENABLE, joinStatus);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userMapper.existsByEmailAndStatus(email, User.Status.ENABLE);
	}
}
