package com.winitech.apiGateway.domain.userSessionBlock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.apiGateway.domain.userSessionBlock
 * └ UserSessionBlock.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:03
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserSessionBlockServiceImpl implements UserSessionBlockService {
	private final UserSessionBlockStore userSessionBlockStore;
	private final UserSessionBlockReader userSessionBlockReader;

	@Override
	public UserSessionBlockInfo registerUserSessionBlock(UserSessionBlockCommand.RegisterRequestCommand userSessionBlockCommand) {
		if (userSessionBlockCommand.getUserSessionId() != null && userSessionBlockReader.existUserSessionBlockByExcludingSelf(userSessionBlockCommand.getUserSessionId(), UUID.randomUUID())) {
			// AccessToken이 이미 존재하는 경우 성공적인것으로 간주하고 이미 등록된 UserSessionBlock을 반환
			return searchUserSessionBlockByUserSessionId(userSessionBlockCommand.getUserSessionId()); 
		}

		UserSessionBlock initUserSessionBlock = UserSessionBlock.builder()
				.userSessionId(userSessionBlockCommand.getUserSessionId())
				.accessTokenExpiresAt(userSessionBlockCommand.getAccessTokenExpiresAt())
				.userId(userSessionBlockCommand.getUserId())
				.build();

		UserSessionBlock userSessionBlock = userSessionBlockStore.store(initUserSessionBlock);
		return new UserSessionBlockInfo(userSessionBlock);
	}

	@Override
	public UserSessionBlockInfo modifyUserSessionBlock(UUID id, UserSessionBlockCommand.ModifyRequestCommand userSessionBlockCommand) {
		UserSessionBlock modifyUserSessionBlock = userSessionBlockReader.getUserSessionBlockById(id);

		UserSessionBlock userSessionBlock = userSessionBlockStore.modify(modifyUserSessionBlock, userSessionBlockCommand);
		return new UserSessionBlockInfo(userSessionBlock);
	}

	@Override
	public void removeUserSessionBlock(UUID id) {
		userSessionBlockStore.remove(id);
	}

	@Override
	public void removeExpiredAccessToken() {
		List<UserSessionBlock> expiredUserSessionBlockList = userSessionBlockReader.getExpiredUserSessionBlock();
		
		if (expiredUserSessionBlockList.size() > 0) {
			userSessionBlockStore.removeAll(expiredUserSessionBlockList);
		}
	}

	@Override
	public UserSessionBlockInfo searchUserSessionBlockById(UUID id) {
		UserSessionBlock userSessionBlock = userSessionBlockReader.getUserSessionBlockById(id);
		return new UserSessionBlockInfo(userSessionBlock);
	}
	
	@Override
	public UserSessionBlockInfo searchUserSessionBlockByUserSessionId(UUID userSessionId) {
		UserSessionBlock userSessionBlock = userSessionBlockReader.getUserSessionBlockByUserSessionId(userSessionId);
		return new UserSessionBlockInfo(userSessionBlock);
	}
	
	@Override
	public List<UserSessionBlockInfo> getAllUserSessionBlock() {
		List<UserSessionBlock> userSessionBlockList = userSessionBlockReader.getAllUserSessionBlock();
		return userSessionBlockList.stream()
				.map(UserSessionBlockInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public boolean existUserSessionBlockByUserSessionId(UUID userSessionId) {
		return userSessionBlockReader.existUserSessionBlockByUserSessionId(userSessionId);
	}
}
