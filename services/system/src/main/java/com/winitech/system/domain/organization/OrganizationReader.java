package com.winitech.system.domain.organization;

import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.domain.organization
 * └ OrganizationReader.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/07/30
 **/
public interface OrganizationReader {
	Organization getOrganizationById(UUID id);

	Organization getOrganizationByCode(String organizationCode);

	Organization getOrganizationByName(String organizationName);

	Boolean existOrganizationByCodeAndExcludingSelf(String organizationCode, UUID id);

	Boolean existOrganizationByNameAndExcludingSelf(String organizationName, UUID id);

	List<Organization> getAllOrganization();

	List<Organization> getAllOrganization(List<UUID> organizationIds);
}