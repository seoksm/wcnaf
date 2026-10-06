package com.winitech.apiGateway.infrastructure.userSessionBlock;

import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlock;
import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlockCommand;
import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlockReader;
import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlockStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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
public class UserSessionBlockStoreImpl implements UserSessionBlockStore {
	private final UserSessionBlockRepository userSessionBlockRepository;
	private final UserSessionBlockReader userSessionBlockReader;

	@Override
	public UserSessionBlock store(UserSessionBlock userSessionBlock) {
		return userSessionBlockRepository.save(userSessionBlock);
	}

	@Override
	public UserSessionBlock modify(UserSessionBlock userSessionBlock, UserSessionBlockCommand.ModifyRequestCommand command) {
		userSessionBlock.setUserSessionId(command.getUserSessionId());

		userSessionBlock.setAccessTokenExpiresAt(command.getAccessTokenExpiresAt());

		userSessionBlock.setUserId(command.getUserId());

		return userSessionBlockRepository.save(userSessionBlock);
	}

	@Override
	public void remove(UUID userSessionBlockId) {
		userSessionBlockRepository.deleteById(userSessionBlockId);
	}

	@Override
	public void removeAll(List<UserSessionBlock> expiredUserSessionBlockList) {
		userSessionBlockRepository.deleteAll(expiredUserSessionBlockList);
	}
}
