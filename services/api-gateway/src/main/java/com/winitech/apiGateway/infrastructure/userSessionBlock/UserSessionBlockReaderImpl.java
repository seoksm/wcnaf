package com.winitech.apiGateway.infrastructure.userSessionBlock;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlock;
import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlockReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.userSessionBlock
 * └ UserSessionBlock.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:03
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserSessionBlockReaderImpl implements UserSessionBlockReader {
	private final UserSessionBlockRepository userSessionBlockRepository;

	@Override
	public UserSessionBlock getUserSessionBlockById(UUID userSessionBlockId) {
		return userSessionBlockRepository.findById(userSessionBlockId).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public UserSessionBlock getUserSessionBlockByUserSessionId(UUID userSessionId) {
		return userSessionBlockRepository.findByUserSessionId(userSessionId).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public UserSessionBlock getUserSessionBlockByUserSessionIdIfExists(UUID userSessionId) {
		return userSessionBlockRepository.findByUserSessionId(userSessionId).orElse(null);
	}

	@Override
	public List<UserSessionBlock> getExpiredUserSessionBlock() {
		return userSessionBlockRepository.findAllByAccessTokenExpiresAtLessThanEqual(OffsetDateTime.now());
	}

	@Override
	public List<UserSessionBlock> getAllUserSessionBlock() {
		return userSessionBlockRepository.findAllByOrderByIdDesc();
	}

	@Override
	public boolean existUserSessionBlockByExcludingSelf(UUID userSessionId, UUID userSessionBlockId) {
		return userSessionBlockRepository.existsByUserSessionIdAndIdNot(userSessionId, userSessionBlockId);
	}

	@Override
	public boolean existUserSessionBlockByUserSessionId(UUID userSessionId) {
		return userSessionBlockRepository.existsByUserSessionId(userSessionId);
	}
}
