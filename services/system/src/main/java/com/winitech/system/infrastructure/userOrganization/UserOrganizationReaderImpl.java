package com.winitech.system.infrastructure.userOrganization;

import com.winitech.system.domain.userOrganization.UserOrganization;
import com.winitech.system.domain.userOrganization.UserOrganizationKey;
import com.winitech.system.domain.userOrganization.UserOrganizationReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.infrastructure.userOrganization
 * └ UserOrganizationReaderImpl.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserOrganizationReaderImpl implements UserOrganizationReader {
	private final UserOrganizationRepository userOrganizationRepository;
	
	@Override
	public List<UserOrganization> getUserOrganizationByOrganizationId(UUID organizationId) {
		return userOrganizationRepository.findById_OrganizationIdAndSystemStatusOrderByUser_FirstNameAscUser_LastNameAsc(organizationId, UserOrganization.SystemStatus.ENABLE);
	}

	@Override
	public List<UserOrganization> getUserOrganizationByUserId(UUID userId) {
		return userOrganizationRepository.findById_UserIdAndSystemStatusOrderByOrganization_OrganizationNameAsc(userId, UserOrganization.SystemStatus.ENABLE);
	}

	@Override
	public List<UserOrganization> getAllUserOrganization() {
		return userOrganizationRepository.findBySystemStatus(UserOrganization.SystemStatus.ENABLE);
	}

	@Override
	public Boolean existUserOrganizationByUserIdAndOrganizationId(UUID userId, UUID organizationId) {
		return userOrganizationRepository.existsById_UserIdAndId_OrganizationIdAndSystemStatus(userId, organizationId, UserOrganization.SystemStatus.ENABLE);
	}

	@Override
	public UserOrganization getUserOrganization(UUID userId, UUID organizationId) {
		return userOrganizationRepository.findById_UserIdAndId_OrganizationIdAndSystemStatus(userId, organizationId, UserOrganization.SystemStatus.ENABLE)
				.orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public UserOrganization getUserOrganizationIfExists(UUID userId, UUID organizationId) {
		return userOrganizationRepository.findById(new UserOrganizationKey(userId, organizationId)).orElse(null);
	}
}
