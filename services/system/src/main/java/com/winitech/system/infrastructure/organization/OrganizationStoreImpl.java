package com.winitech.system.infrastructure.organization;

import com.winitech.system.domain.organization.Organization;
import com.winitech.system.domain.organization.OrganizationCommand;
import com.winitech.system.domain.organization.OrganizationStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * com.winitech.system.infrastructure.organization
 * └ OrganizationStoreImpl.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/07/30
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationStoreImpl implements OrganizationStore {
	private final OrganizationRepository organizationRepository;

	@Override
	public Organization store(Organization organization) {
		return organizationRepository.save(organization);
	}

	@Override
	public Organization modify(Organization organization, OrganizationCommand command) {
		organization.setOrganizationCode(command.getOrganizationCode());
		organization.setOrganizationName(command.getOrganizationName());
		organization.setDatabaseHost(command.getDatabaseHost());
		organization.setDatabasePort(command.getDatabasePort());
		organization.setDatabaseMessage(command.getDatabaseMessage());
		organization.setTenantSchemaVersion(command.getTenantSchemaVersion());
		organization.setTenantSetupStatus(command.getTenantSetupStatus());
		organization.setTenantStatus(command.getTenantStatus());

		return organizationRepository.save(organization);
	}
}
