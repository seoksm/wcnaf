package com.winitech.system.domain.userOrganization;

import lombok.Getter;

import java.util.UUID;

/**
 * com.winitech.system.domain.userOrganization
 * └ UserOrganizationInfo.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
@Getter
public class UserOrganizationInfo {
	private final UUID userId;
	private final UUID organizationId;
	private final String organizationCode;
	private final String organizationName;
	private final UserOrganization.SystemStatus systemStatus;

	public UserOrganizationInfo(UserOrganization userOrganization) {
		this.userId = userOrganization.getId().getUserId();
		this.organizationId = userOrganization.getId().getOrganizationId();
		this.organizationCode = userOrganization.getOrganization().getOrganizationCode();
		this.organizationName = userOrganization.getOrganization().getOrganizationName();
		this.systemStatus = userOrganization.getSystemStatus();
	}
}
