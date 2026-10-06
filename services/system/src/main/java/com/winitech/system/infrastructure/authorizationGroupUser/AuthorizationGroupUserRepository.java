package com.winitech.system.infrastructure.authorizationGroupUser;

import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.authorizationGroupUser
 * └ AuthorizationGroupUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-20 10:09
 **/
public interface AuthorizationGroupUserRepository extends JpaRepository<AuthorizationGroupUser, UUID> {
//	List<AuthorizationGroupUser> findAllBySystemStatusOrderByIdDesc(AuthorizationGroupUser.SystemStatus systemStatus);

//	List<AuthorizationGroupUser> findAllBySystemStatusOrderByIdDesc(AuthorizationGroupUser.SystemStatus systemStatus, Sort sort);

	Optional<AuthorizationGroupUser> findByAuthorizationGroupIdAndUserId(UUID authorizationGroupId, UUID userId);

//	Optional<AuthorizationGroupUser> findByIdAndSystemStatus(UUID authorizationGroupUserId, AuthorizationGroupUser.SystemStatus systemStatus);

	List<AuthorizationGroupUser> findByAuthorizationGroupId(UUID authorizationGroupId);
	
	List<AuthorizationGroupUser> findByAuthorizationGroupIdAndUserIdIn(UUID authorizationGroupId, List<UUID> userIdList);

	void deleteByAuthorizationGroupIdAndUserId(UUID authorizationGroupId, UUID userId);

	boolean existsByAuthorizationGroupIdAndUserIdAndIdNot(UUID authorizationGroupId, UUID userId, UUID authorizationGroupUserId);

	@Query("" +
			"SELECT agu " +
			"  FROM AuthorizationGroup ag " +
			"  JOIN AuthorizationGroupUser agu ON agu.authorizationGroup = ag" +
			" WHERE ag.id IN :authorizationGroupIdList" +
			"   AND ag.status = 'ENABLE' " +
			"   AND ag.systemStatus = 'ENABLE'")
	List<AuthorizationGroupUser> findActiveByAuthorizationGroupIdIn(@Param("authorizationGroupIdList") List<UUID> authorizationGroupIdList);
}
