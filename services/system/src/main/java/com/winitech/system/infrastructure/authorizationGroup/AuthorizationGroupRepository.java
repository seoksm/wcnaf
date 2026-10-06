package com.winitech.system.infrastructure.authorizationGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.winitech.system.domain.authorizationGroup.AuthorizationGroup;

/**
 * com.winitech.system.infrastructure.userDepartment
 * └ UserOrganizationRepository.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
public interface AuthorizationGroupRepository extends JpaRepository<AuthorizationGroup, UUID> {
	Optional<AuthorizationGroup> findByIdAndSystemStatus(UUID id, AuthorizationGroup.SystemStatus systemStatus);
	
	Optional<AuthorizationGroup> findByGroupCodeAndSystemStatus(String groupCode, AuthorizationGroup.SystemStatus systemStatus);
	
	List<AuthorizationGroup> findAllBySystemStatusOrderByGroupName(AuthorizationGroup.SystemStatus systemStatus);

	Boolean existsByGroupCodeAndIdNotAndSystemStatus(String groupCode, UUID id, AuthorizationGroup.SystemStatus systemStatus);
}
