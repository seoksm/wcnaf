package com.winitech.system.infrastructure.userOrganization;

import com.winitech.system.domain.userOrganization.UserOrganization;
import com.winitech.system.domain.userOrganization.UserOrganizationCommand;
import com.winitech.system.domain.userOrganization.UserOrganizationStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * com.winitech.system.infrastructure.userOrganization
 * └ UserOrganizationStoreImpl.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserOrganizationStoreImpl implements UserOrganizationStore {
	private final UserOrganizationRepository userOrganizationRepository;

	@Override
	public UserOrganization modify(UserOrganization userOrganization, UserOrganizationCommand command) {
		return userOrganizationRepository.save(userOrganization);
	}

	@Override
	public UserOrganization store(UserOrganization userOrganization) {
		return userOrganizationRepository.save(userOrganization);
	}
}
