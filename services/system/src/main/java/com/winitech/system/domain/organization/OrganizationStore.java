package com.winitech.system.domain.organization;

/**
 * com.winitech.system.domain.organization
 * └ OrganizationStore.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/07/30
 **/
public interface OrganizationStore {
	Organization store(Organization organization);

	Organization modify(Organization organization, OrganizationCommand command);
}
