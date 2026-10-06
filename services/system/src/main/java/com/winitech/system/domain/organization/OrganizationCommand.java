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
@Builder
@ToString
public class OrganizationCommand {

	private final UUID id;
	private final String organizationCode;
	private final String organizationName;
	private final String databaseHost;
	private final Integer databasePort;
	private final String databaseMessage;
	private final Integer tenantSchemaVersion;
	private final Organization.TenantSetupStatus tenantSetupStatus;
	private final Organization.TenantStatus tenantStatus;
	private final String nationalCode;

	public Organization toEntity() {
		return Organization.builder()
				.id(id)
				.organizationCode(organizationCode)
				.organizationName(organizationName)
				.databaseHost(databaseHost)
				.databasePort(databasePort)
				.databaseMessage(databaseMessage)
				.tenantSchemaVersion(tenantSchemaVersion)
				.tenantSetupStatus(tenantSetupStatus)
				.tenantStatus(tenantStatus)
				.build();
	}
}
