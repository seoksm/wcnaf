package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonAuthorizationGroupUser;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserCommand;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserStore;
import com.winitech.common.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationGroupUserStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 13:32
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonAuthorizationGroupUserStoreImpl implements CommonAuthorizationGroupUserStore {
	private final CommonAuthorizationGroupUserRepository commonAuthorizationGroupUserRepository;

	@Override
	public CommonAuthorizationGroupUser store(CommonAuthorizationGroupUser commonAuthorizationGroupUser) {
		return commonAuthorizationGroupUserRepository.save(commonAuthorizationGroupUser);
	}

	@Override
	public CommonAuthorizationGroupUser modify(CommonAuthorizationGroupUser commonAuthorizationGroupUser, CommonAuthorizationGroupUserCommand command) {
		commonAuthorizationGroupUser.setAuthorizationGroupId(command.getAuthorizationGroupId());
		commonAuthorizationGroupUser.setUserId(command.getUserId());

		return commonAuthorizationGroupUserRepository.save(commonAuthorizationGroupUser);
	}

	@Override
	public void storeAll(List<CommonAuthorizationGroupUser> toSaveList) {
		commonAuthorizationGroupUserRepository.saveAll(toSaveList);
	}

	@Override
	public void remove(UUID userId, UUID authorizationGroupId) {
		commonAuthorizationGroupUserRepository.deleteByUserIdAndAuthorizationGroupId(userId, authorizationGroupId);
	}

	@Override
	public void removeAll(List<CommonAuthorizationGroupUser> toDeleteList) {
		commonAuthorizationGroupUserRepository.deleteAll(toDeleteList);
	}
}
