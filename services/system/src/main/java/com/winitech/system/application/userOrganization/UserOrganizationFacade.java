package com.winitech.system.application.userOrganization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.winitech.system.domain.organization.OrganizationInfo;
import com.winitech.system.domain.organization.OrganizationService;
import com.winitech.system.domain.user.UserInfo;
import com.winitech.system.domain.user.UserService;
import com.winitech.system.domain.userOrganization.UserOrganizationCommand;
import com.winitech.system.domain.userOrganization.UserOrganizationInfo;
import com.winitech.system.domain.userOrganization.UserOrganizationService;
import com.winitech.system.interfaces.inboundAdapter.web.user.UserProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * com.winitech.system.application.userOrganization
 * └ UserOrganizationFacade.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserOrganizationFacade {
	private final UserOrganizationService userOrganizationService;
	private final OrganizationService organizationService; 
	private final UserService userService;
	private final UserProducer userProducer;

	public UserOrganizationInfo registerUserOrganization(UserOrganizationCommand command) throws JsonProcessingException {
		UserOrganizationInfo userOrganizationInfo = userOrganizationService.registerUserOrganization(command);

		UserInfo userInfo = userService.searchUserInfo(userOrganizationInfo.getUserId());
		userProducer.userUpdated(null, new UserInfo.UserCdcInfo(userInfo, userOrganizationService));
		
		return userOrganizationInfo;
	}

	public UserOrganizationInfo modifyUserOrganization(UUID userId, UUID organizationId, UserOrganizationCommand command) {
		return userOrganizationService.modifyUserOrganization(userId, organizationId, command);
	}

	public void removeUserOrganization(UUID userId, UUID organizationId) throws JsonProcessingException {
		userOrganizationService.removeUserOrganization(userId, organizationId);

		OrganizationInfo organizationInfo = organizationService.searchOrganizationById(organizationId);

		if (organizationInfo != null) {
			String organizationCode = organizationInfo.getOrganizationCode();

			userProducer.userOrganizationRemove(new UserInfo.UserOrganizationCdcInfo(userId, organizationId, organizationCode));
		}
	}

	public UserOrganizationInfo getUserOrganization(UUID userId, UUID organizationId) {
		return userOrganizationService.getUserOrganization(userId, organizationId);
	}

	public Boolean existUserOrganizationByUserIdAndOrganizationId(UUID userId, UUID organizationId) {
		return userOrganizationService.existUserOrganizationByUserIdAndOrganizationId(userId, organizationId);
	}
	
	public List<UserOrganizationInfo> getAllUserOrganization() {
		return userOrganizationService.getAllUserOrganization();
	}

	public List<UserOrganizationInfo> getUserOrganizationByUserId(UUID userId) {
		return userOrganizationService.getUserOrganizationByUserId(userId);
	}

	public List<UserOrganizationInfo> getUserOrganizationByOrganizationId(UUID organizationId) {
		return userOrganizationService.getUserOrganizationByOrganizationId(organizationId);
	}

	public List<UserInfo> getAllUserByOrganization(UUID organizationId) {
		List<UUID> userIdList = userOrganizationService.getUserOrganizationByOrganizationId(organizationId).stream()
				.map(UserOrganizationInfo::getUserId)
				.collect(Collectors.toList());
		
		return userService.searchAllUserInfo(userIdList);
	}

	public List<OrganizationInfo> getAllOrganizationByUser(UUID userId) {
		List<UUID> organizationIdList = userOrganizationService.getUserOrganizationByUserId(userId).stream()
				.map(UserOrganizationInfo::getOrganizationId)
				.collect(Collectors.toList());

		return organizationService.getAllOrganization(organizationIdList);
	}
}
