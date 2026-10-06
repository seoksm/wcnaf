package com.winitech.system.domain.userOrganization;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.system.domain.organization.OrganizationReader;
import com.winitech.system.domain.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * com.winitech.system.domain.userOrganization
 * └ UserOrganizationServiceImpl.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserOrganizationServiceImpl extends EgovAbstractServiceImpl implements UserOrganizationService {
	private final UserOrganizationStore userOrganizationStore;
	private final UserOrganizationReader userOrganizationReader;
	private final UserReader userReader;
	private final OrganizationReader organizationReader;

	@Override
	public UserOrganizationInfo registerUserOrganization(UserOrganizationCommand command) {
		if (userOrganizationReader.existUserOrganizationByUserIdAndOrganizationId(command.getUserId(), command.getOrganizationId())) {
			throw new IllegalStatusException("Already joined user.");
		}

		UserOrganization userOrganization = userOrganizationReader.getUserOrganizationIfExists(command.getUserId(), command.getOrganizationId());
		
		if (userOrganization == null) {
			userOrganization = UserOrganization.builder()
					.user(userReader.getUser(command.getUserId()))
					.organization(organizationReader.getOrganizationById(command.getOrganizationId()))
					.build();
		}
		
		userOrganization.enable();
		userOrganizationStore.store(userOrganization);
		
		return new UserOrganizationInfo(userOrganization);
	}

	@Override
	public UserOrganizationInfo modifyUserOrganization(UUID userId, UUID organizationId, UserOrganizationCommand command) {
		UserOrganization userOrganization = userOrganizationReader.getUserOrganizationIfExists(userId, organizationId);
		
		if (userOrganization == null) {
			throw new EntityNotFoundException("UserOrganization not found.");
		}
		
		userOrganizationStore.store(userOrganization);

		return new UserOrganizationInfo(userOrganization);
	}

	@Override
	public void removeUserOrganization(UUID userId, UUID organizationId) {
		UserOrganization userOrganization = userOrganizationReader.getUserOrganizationIfExists(userId, organizationId);
		if (userOrganization != null) {
			userOrganization.disable();
			userOrganizationStore.store(userOrganization);
		}
}

	@Override
	public UserOrganizationInfo getUserOrganization(UUID userId, UUID organizationId) {
		return new UserOrganizationInfo(userOrganizationReader.getUserOrganization(userId, organizationId));
	}

	@Override
	public Boolean existUserOrganizationByUserIdAndOrganizationId(UUID userId, UUID organizationId) {
		return userOrganizationReader.existUserOrganizationByUserIdAndOrganizationId(userId, organizationId);
	}

	@Override
	public List<UserOrganizationInfo> getAllUserOrganization() {
		return userOrganizationReader.getAllUserOrganization()
				.stream()
				.map(UserOrganizationInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<UserOrganizationInfo> getUserOrganizationByUserId(UUID userId) {
		return userOrganizationReader.getUserOrganizationByUserId(userId)
				.stream()
				.map(UserOrganizationInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<UserOrganizationInfo> getUserOrganizationByOrganizationId(UUID organizationId) {
		return userOrganizationReader.getUserOrganizationByOrganizationId(organizationId)
				.stream()
				.map(UserOrganizationInfo::new)
				.collect(Collectors.toList());
	}
}
