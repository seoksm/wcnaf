package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermission;
import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionReader;
import com.winitech.common.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationGroupPermissionReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 13:31
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonAuthorizationGroupPermissionReaderImpl implements CommonAuthorizationGroupPermissionReader {
	private final CommonAuthorizationGroupPermissionRepository commonAuthorizationGroupPermissionRepository;

	@Override
	public CommonAuthorizationGroupPermission getCommonAuthorizationGroupPermissionById(UUID authorizationGroupId, UUID menuId) {
		return commonAuthorizationGroupPermissionRepository.findByAuthorizationGroupIdAndMenuId(authorizationGroupId, menuId).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public CommonAuthorizationGroupPermission getCommonAuthorizationGroupPermissionByIdIfExists(UUID authorizationGroupId, UUID menuId) {
		return commonAuthorizationGroupPermissionRepository.findByAuthorizationGroupIdAndMenuId(authorizationGroupId, menuId).orElse(null);
	}

//	@Override
//	public List<CommonAuthorizationGroupPermission> getCommonAuthorizationGroupPermissionByName(String name) {
//		return List.of();
//	}

	@Override
	public List<CommonAuthorizationGroupPermission> getAllCommonAuthorizationGroupPermission() {
		return commonAuthorizationGroupPermissionRepository.findAll();
	}

	@Override
	public boolean isExistCommonAuthorizationGroupPermissionById(UUID authorizationGroupId, UUID menuId) {
		return commonAuthorizationGroupPermissionRepository. existsByAuthorizationGroupIdAndMenuId(authorizationGroupId, menuId);
	}

	@Override
	public List<CommonAuthorizationGroupPermission> getListByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList) {
		return commonAuthorizationGroupPermissionRepository.findByAuthorizationGroupIdIn(authorizationGroupIdList);
	}
}
