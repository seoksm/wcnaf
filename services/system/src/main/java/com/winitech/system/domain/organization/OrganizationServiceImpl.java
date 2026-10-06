package com.winitech.system.domain.organization;

import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.WiniSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * com.winitech.system.domain.organization
 * └ OrganizationServiceImpl.java
 * @author : coding (클라우드팀)
 * @since : 2024/07/30
 * @see : None
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl extends EgovAbstractServiceImpl implements OrganizationService{

    private final OrganizationStore organizationStore;
    private final OrganizationReader organizationReader;

    @Override
    public OrganizationInfo registerOrganization(OrganizationCommand organizationCommand) {
        if(Boolean.TRUE.equals(organizationReader.existOrganizationByCodeAndExcludingSelf(organizationCommand.getOrganizationCode(), UUID.randomUUID()))) {
            throw new IllegalStatusException("Already registered Organization Code.");
        }
        
        WiniSecurity.checkValidOrganizationCodeFormat(organizationCommand.getOrganizationCode());
        
        Organization initOrganization = Organization.builder()
                .organizationCode(organizationCommand.getOrganizationCode())
                .organizationName(organizationCommand.getOrganizationName())
                .databaseHost(organizationCommand.getDatabaseHost())
                .databasePort(organizationCommand.getDatabasePort())
                .tenantSetupStatus(Organization.TenantSetupStatus.NONE)
                .tenantStatus(Organization.TenantStatus.UNKNOWN)
                .build();
        Organization organization = organizationStore.store(initOrganization);
        return new OrganizationInfo(organization);
    }

    @Override
    public OrganizationInfo modifyOrganization(UUID id, OrganizationCommand organizationCommand) {
        if(Boolean.TRUE.equals(organizationReader.existOrganizationByCodeAndExcludingSelf(organizationCommand.getOrganizationCode(), id))) {
            throw new IllegalStatusException("Already registered Organization Code.");
        }

        WiniSecurity.checkValidOrganizationCodeFormat(organizationCommand.getOrganizationCode());

        Organization modifyOrganization = organizationReader.getOrganizationById(id);
        modifyOrganization.setOrganizationCode(organizationCommand.getOrganizationCode());
        modifyOrganization.setOrganizationName(organizationCommand.getOrganizationName());
        modifyOrganization.setDatabaseHost(organizationCommand.getDatabaseHost());
        modifyOrganization.setDatabasePort(organizationCommand.getDatabasePort());
        Organization organization = organizationStore.store(modifyOrganization);
        return new OrganizationInfo(organization);
    }

    @Override
    public void removeOrganization(UUID id) {
        Organization organization = organizationReader.getOrganizationById(id);
        organization.disable();
        organizationStore.store(organization);
    }

    @Override
    public OrganizationInfo searchOrganizationById(UUID id) {
        Organization organization = organizationReader.getOrganizationById(id);
        return new OrganizationInfo(organization);
    }

    @Override
    public OrganizationInfo searchOrganizationByOrganizationCode(String organizationCode) {
        Organization organization = organizationReader.getOrganizationByCode(organizationCode);
        return new OrganizationInfo(organization);
    }

    @Override
    public OrganizationInfo searchOrganizationByOrganizationName(String organizationName) {
        Organization organization = organizationReader.getOrganizationByName(organizationName);
        return new OrganizationInfo(organization);        
    }

    @Override
    public List<OrganizationInfo> getAllOrganization() {
        List<Organization> organizationList = organizationReader.getAllOrganization();
        return organizationList.stream().map(OrganizationInfo::new).collect(Collectors.toList());
    }

    @Override
    public List<OrganizationInfo> getAllOrganization(List<UUID> organizationIds) {
        List<Organization> organizationList = organizationReader.getAllOrganization(organizationIds);
        return organizationList.stream().map(OrganizationInfo::new).collect(Collectors.toList());
    }

    @Override
    public OrganizationInfo setTenantSetupStatusToPendingIfNone(UUID id) {
        Organization organization = organizationReader.getOrganizationById(id);
        
        if (organization.getTenantSetupStatus() == null || organization.getTenantSetupStatus() == Organization.TenantSetupStatus.NONE) {
            organization.setTenantSetupStatus(Organization.TenantSetupStatus.PENDING);
            organizationStore.store(organization);
        }
        
        return new OrganizationInfo(organization);
    }

    @Override
    public OrganizationInfo setTenantSetupStatus(UUID id, Organization.TenantSetupStatus tenantSetupStatus, Integer tenantSchemaVersion) {
        Organization organization = organizationReader.getOrganizationById(id);
        
        organization.setTenantSetupStatus(tenantSetupStatus);

        if (tenantSchemaVersion != null) {
            // 테넌트 버전은 파라미터로 넘어 올 때만 업데이트

            organization.setTenantSchemaVersion(tenantSchemaVersion);
        }
        
        organizationStore.store(organization);
    
        return new OrganizationInfo(organization);
    }
}
