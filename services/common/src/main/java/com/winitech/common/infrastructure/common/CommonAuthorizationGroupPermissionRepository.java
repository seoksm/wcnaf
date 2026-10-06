package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermission;
import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationGroupPermissionRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 13:31
 **/
public interface CommonAuthorizationGroupPermissionRepository extends JpaRepository<CommonAuthorizationGroupPermission, CommonAuthorizationGroupPermissionId> {
	void deleteByAuthorizationGroupIdAndMenuId(UUID authorizationGroupId, UUID menuId);

	Optional<CommonAuthorizationGroupPermission> findByAuthorizationGroupIdAndMenuId(UUID authorizationGroupId, UUID menuId);

	boolean existsByAuthorizationGroupIdAndMenuId(UUID authorizationGroupId, UUID menuId);

	List<CommonAuthorizationGroupPermission> findByAuthorizationGroupIdIn(List<UUID> authorizationGroupIdList);
}
