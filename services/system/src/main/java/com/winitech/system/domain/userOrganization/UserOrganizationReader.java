package com.winitech.system.domain.userOrganization;

import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.domain.userOrganization
 * └ UserOrganizationReader.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
public interface UserOrganizationReader {
	UserOrganization getUserOrganization(UUID userId, UUID organizationId);
	
	UserOrganization getUserOrganizationIfExists(UUID userId, UUID organizationId);

	Boolean existUserOrganizationByUserIdAndOrganizationId(UUID userId, UUID organizationId);

	List<UserOrganization> getAllUserOrganization();
	
	List<UserOrganization> getUserOrganizationByUserId(UUID userId);
	
	List<UserOrganization> getUserOrganizationByOrganizationId(UUID organizationId);
}