package com.winitech.system.domain.userOrganization;

/**
 * com.winitech.system.domain.userOrganization
 * └ UserOrganizationStore.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
public interface UserOrganizationStore {
	UserOrganization store(UserOrganization userOrganization);

	UserOrganization modify(UserOrganization userOrganization, UserOrganizationCommand command);
}
