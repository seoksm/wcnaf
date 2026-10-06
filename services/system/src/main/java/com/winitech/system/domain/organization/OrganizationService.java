package com.winitech.system.domain.organization;
import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.domain.organization
 * └ OrganizationService.java
 * @author : coding (클라우드팀)
 * @since : 2024/07/30
 * @see : None
 **/
public interface OrganizationService {
    OrganizationInfo registerOrganization(OrganizationCommand organizationCommand);
    OrganizationInfo modifyOrganization(UUID id, OrganizationCommand organizationCommand);

    void removeOrganization(UUID id);

    OrganizationInfo searchOrganizationById(UUID id);

    OrganizationInfo searchOrganizationByOrganizationCode(String organizationCode);
    
    OrganizationInfo searchOrganizationByOrganizationName(String organizationName);

    List<OrganizationInfo> getAllOrganization();

    List<OrganizationInfo> getAllOrganization(List<UUID> organizationIds);

	OrganizationInfo setTenantSetupStatusToPendingIfNone(UUID id);

    OrganizationInfo setTenantSetupStatus(UUID id, Organization.TenantSetupStatus tenantSetupStatus, Integer tenantSchemaVersion);
}
