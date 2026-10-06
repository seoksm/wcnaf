package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonAuthorizationGroupUser;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserReader;
import com.winitech.common.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationGroupUserReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 13:32
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonAuthorizationGroupUserReaderImpl implements CommonAuthorizationGroupUserReader {
	private final CommonAuthorizationGroupUserRepository commonAuthorizationGroupUserRepository;

	@Override
	public CommonAuthorizationGroupUser getCommonAuthorizationGroupUserById(UUID userId, UUID authorizationGroupId) {
		return commonAuthorizationGroupUserRepository.findByUserIdAndAuthorizationGroupId(userId, authorizationGroupId).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public CommonAuthorizationGroupUser getCommonAuthorizationGroupUserByIdIfExists(UUID userId, UUID authorizationGroupId) {
		return commonAuthorizationGroupUserRepository.findByUserIdAndAuthorizationGroupId(userId, authorizationGroupId).orElse(null);
	}

//	@Override
//	public List<CommonAuthorizationGroupUser> getCommonAuthorizationGroupUserByName(String name) {
//		return List.of();
//	}

	@Override
	public List<CommonAuthorizationGroupUser> getAllCommonAuthorizationGroupUser() {
		return commonAuthorizationGroupUserRepository.findAll();
	}

	@Override
	public boolean isExistCommonAuthorizationGroupUserById(UUID userId, UUID authorizationGroupId) {
		return commonAuthorizationGroupUserRepository.existsByUserIdAndAuthorizationGroupId(userId, authorizationGroupId);
	}

	@Override
	public List<CommonAuthorizationGroupUser> getListByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList) {
		return commonAuthorizationGroupUserRepository.findByAuthorizationGroupIdIn(authorizationGroupIdList);
	}
}
