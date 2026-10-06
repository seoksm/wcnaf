package com.winitech.system.domain.userOrganization;

import com.winitech.system.domain.organization.Organization;
import com.winitech.system.domain.organization.OrganizationReader;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserReader;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * com.winitech.system.domain.userOrganization
 * └ UserOrganizationCommand.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
@Getter
@Builder
@ToString
public class UserOrganizationCommand {
	private final UserReader userReader;
	private final OrganizationReader organizationReader;
	
	private final UUID userId;
	private final UUID organizationId;
	private final String userGroupCode;
	private final UserOrganization.SystemStatus systemStatus;
	
	public UserOrganization toEntity() {
		User user = userReader.getUser(userId);
		Organization organization = organizationReader.getOrganizationById(organizationId);
		
		return UserOrganization.builder()
				.user(user)
				.organization(organization)
				.systemStatus(systemStatus)
				.build();
	}
}
