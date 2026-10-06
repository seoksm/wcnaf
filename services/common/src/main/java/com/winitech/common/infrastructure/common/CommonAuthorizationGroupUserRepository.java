package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonAuthorizationGroupUser;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserId;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationGroupUserRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 13:32
 **/
public interface CommonAuthorizationGroupUserRepository extends JpaRepository<CommonAuthorizationGroupUser, CommonAuthorizationGroupUserId> {
	void deleteByUserIdAndAuthorizationGroupId(UUID userId, UUID authorizationGroupId);

	Optional<CommonAuthorizationGroupUser> findByUserIdAndAuthorizationGroupId(UUID userId, UUID authorizationGroupId);

	boolean existsByUserIdAndAuthorizationGroupId(UUID userId, UUID authorizationGroupId);

	List<CommonAuthorizationGroupUser> findByAuthorizationGroupIdIn(List<UUID> authorizationGroupIdList);
}
