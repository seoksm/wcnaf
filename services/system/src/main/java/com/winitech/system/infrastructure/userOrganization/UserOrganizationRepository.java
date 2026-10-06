package com.winitech.system.infrastructure.userOrganization;

import com.winitech.system.domain.userOrganization.UserOrganization;
import com.winitech.system.domain.userOrganization.UserOrganizationKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * com.winitech.system.infrastructure.userOrganization
 * └ UserOrganizationRepository.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
public interface UserOrganizationRepository extends JpaRepository<UserOrganization, UserOrganizationKey> {
	List<UserOrganization> findById_OrganizationIdAndSystemStatusOrderByUser_FirstNameAscUser_LastNameAsc(UUID organizationId, UserOrganization.SystemStatus systemStatus);
	
	List<UserOrganization> findById_UserIdAndSystemStatusOrderByOrganization_OrganizationNameAsc(UUID userId, UserOrganization.SystemStatus systemStatus);

	List<UserOrganization> findBySystemStatus(UserOrganization.SystemStatus systemStatus);

	Optional<UserOrganization> findById_UserIdAndId_OrganizationIdAndSystemStatus(UUID userId, UUID organizationId, UserOrganization.SystemStatus systemStatus);
	
	boolean existsById_UserIdAndId_OrganizationIdAndSystemStatus(UUID userId, UUID organizationId, UserOrganization.SystemStatus systemStatus);
}
