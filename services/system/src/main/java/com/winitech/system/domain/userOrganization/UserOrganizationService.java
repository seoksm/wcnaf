package com.winitech.system.domain.userOrganization;

import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.domain.userOrganization
 * └ UserOrganizationService.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
public interface UserOrganizationService {
	UserOrganizationInfo registerUserOrganization(UserOrganizationCommand command);

	UserOrganizationInfo modifyUserOrganization(UUID userId, UUID organizationId, UserOrganizationCommand command);

	void removeUserOrganization(UUID userId, UUID organizationId);

	UserOrganizationInfo getUserOrganization(UUID userId, UUID organizationId);

	Boolean existUserOrganizationByUserIdAndOrganizationId(UUID userId, UUID organizationId);

	List<UserOrganizationInfo> getAllUserOrganization();

	List<UserOrganizationInfo> getUserOrganizationByUserId(UUID userId);

	List<UserOrganizationInfo> getUserOrganizationByOrganizationId(UUID organizationId);
}
