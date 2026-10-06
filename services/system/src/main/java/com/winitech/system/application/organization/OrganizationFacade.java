package com.winitech.system.application.organization;

import com.winitech.system.domain.organization.OrganizationCommand;
import com.winitech.system.domain.organization.OrganizationInfo;
import com.winitech.system.domain.organization.OrganizationService;
import com.winitech.system.domain.userOrganization.UserOrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.application.organization
 * └ OrganizationFacade.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/07/30
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationFacade {
	private final OrganizationService organizationService;

	public OrganizationInfo registerOrganization(OrganizationCommand organizationCommand) {
		return organizationService.registerOrganization(organizationCommand);
	}

	public OrganizationInfo modifyOrganization(UUID id, OrganizationCommand organizationCommand) {
		return organizationService.modifyOrganization(id, organizationCommand);
	}

	public void removeOrganization(UUID id) {
		organizationService.removeOrganization(id);
	}

	public OrganizationInfo searchOrganizationById(UUID id) {
		return organizationService.searchOrganizationById(id);
	}

	public OrganizationInfo searchOrganizationByOrganizationCode(String organizationCode) {
		return organizationService.searchOrganizationByOrganizationName(organizationCode);
	}

	public OrganizationInfo searchOrganizationByOrganizationName(String organizationName) {
		return organizationService.searchOrganizationByOrganizationName(organizationName);
	}

	public List<OrganizationInfo> getAllOrganization() {
		return organizationService.getAllOrganization();
	}

	public OrganizationInfo setTenantSetupStatusToPendingIfNone(UUID id) {
		return organizationService.setTenantSetupStatusToPendingIfNone(id);
	}
}
