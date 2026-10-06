package com.winitech.system.infrastructure.organization;

import com.winitech.system.domain.organization.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * com.winitech.system.infrastructure.organization
 * └ OrganizationRepository.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/07/30
 **/
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
	List<Organization> findAllBySystemStatus(Organization.SystemStatus systemStatus);
	
	Boolean existsOrganizationByOrganizationCodeAndSystemStatusAndIdNot(String organizationCode, Organization.SystemStatus systemStatus, UUID id);
	
	Boolean existsOrganizationByOrganizationNameAndSystemStatusAndIdNot(String organizationName, Organization.SystemStatus systemStatus, UUID id);

	Optional<Organization> findByIdAndSystemStatus(UUID id, Organization.SystemStatus systemStatus);

	Optional<Organization> findByOrganizationCodeAndSystemStatus(String name, Organization.SystemStatus systemStatus);

	Optional<Organization> findByOrganizationNameAndSystemStatus(String name, Organization.SystemStatus systemStatus);
	
	List<Organization> findAllBySystemStatusAndIdIn(Organization.SystemStatus systemStatus, List<UUID> organizationIds);
}
