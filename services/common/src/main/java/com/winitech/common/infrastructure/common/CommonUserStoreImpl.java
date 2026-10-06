package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonUser;
import com.winitech.common.domain.common.CommonUserCommand;
import com.winitech.common.domain.common.CommonUserStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonUserStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 12:59
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonUserStoreImpl implements CommonUserStore {
	private final CommonUserRepository commonUserRepository;

	@Override
	public CommonUser store(CommonUser commonUser) {
		return commonUserRepository.save(commonUser);
	}

	@Override
	public CommonUser modify(CommonUser commonUser, CommonUserCommand command) {
		commonUser.setFirstName(command.getFirstName());
		commonUser.setLastName(command.getLastName());
		commonUser.setFullName(command.getFullName());
		commonUser.setEmail(command.getEmail());
		commonUser.setPhoneNumber(command.getPhoneNumber());
		commonUser.setEmployeeNo(command.getEmployeeNo());
		commonUser.setDutyName(command.getDutyName());
		commonUser.setDepartmentName(command.getDepartmentName());

		return commonUserRepository.save(commonUser);
	}
}
