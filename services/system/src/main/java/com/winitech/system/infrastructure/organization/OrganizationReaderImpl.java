package com.winitech.system.infrastructure.organization;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.system.domain.organization.Organization;
import com.winitech.system.domain.organization.OrganizationReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.infrastructure.organization
 * └ OrganizationReaderImpl.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/07/30
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class OrganizationReaderImpl implements OrganizationReader {
	private final OrganizationRepository organizationRepository;

	@Override
	public Organization getOrganizationById(UUID id) {
		return organizationRepository.findByIdAndSystemStatus(id, Organization.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public Organization getOrganizationByCode(String organizationCode) {
		return organizationRepository.findByOrganizationCodeAndSystemStatus(organizationCode, Organization.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public Organization getOrganizationByName(String name) {
		return organizationRepository.findByOrganizationNameAndSystemStatus(name, Organization.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public Boolean existOrganizationByCodeAndExcludingSelf(String organizationCode, UUID id) {
		return organizationRepository.existsOrganizationByOrganizationCodeAndSystemStatusAndIdNot(organizationCode, Organization.SystemStatus.ENABLE, id);
	}

	@Override
	public Boolean existOrganizationByNameAndExcludingSelf(String name, UUID id) {
		return organizationRepository.existsOrganizationByOrganizationNameAndSystemStatusAndIdNot(name, Organization.SystemStatus.ENABLE, id);
	}

	@Override
	public List<Organization> getAllOrganization() {
		return organizationRepository.findAllBySystemStatus(Organization.SystemStatus.ENABLE);
	}

	@Override
	public List<Organization> getAllOrganization(List<UUID> organizationIds) {
		return organizationRepository.findAllBySystemStatusAndIdIn(Organization.SystemStatus.ENABLE, organizationIds);
	}
}
