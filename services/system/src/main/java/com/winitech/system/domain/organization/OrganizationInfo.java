package com.winitech.system.domain.organization;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * com.winitech.system.domain.organization
 * └ Organization.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/07/30
 **/
@Getter
public class OrganizationInfo {
	private final String id;
	private final String organizationCode;
	private final String organizationName;
	private final String databaseHost;
	private final Integer databasePort;
	private final String databaseMessage;
	private final Integer tenantSchemaVersion;
	private final Organization.TenantSetupStatus tenantSetupStatus;
	private final Organization.TenantStatus tenantStatus;

	public OrganizationInfo(Organization organization) {
		this.id = organization.getId().toString();
		this.organizationCode = organization.getOrganizationCode();
		this.organizationName = organization.getOrganizationName();
		this.databaseHost = organization.getDatabaseHost();
		this.databasePort = organization.getDatabasePort();
		this.databaseMessage = organization.getDatabaseMessage();
		this.tenantSchemaVersion = organization.getTenantSchemaVersion();
		this.tenantSetupStatus = organization.getTenantSetupStatus();
		this.tenantStatus = organization.getTenantStatus();
	}
}

