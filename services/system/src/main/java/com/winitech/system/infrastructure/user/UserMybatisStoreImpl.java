package com.winitech.system.infrastructure.user;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.library.WiniCom;
import com.winitech.system.domain.department.Department;
import com.winitech.system.domain.department.DepartmentReader;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserCommand;
import com.winitech.system.domain.user.UserMybatisStore;
import com.winitech.system.domain.user.UserStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * com.winitech.system.infrastructure.user
 * └ UserMybatisStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-06-16 15:08
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserMybatisStoreImpl implements UserMybatisStore {
	private final UserMapper userMapper;
	private final DepartmentReader departmentReader;

	@Override
	public User store(User user) {
		if(StringUtils.isEmpty(user.getFirstName())) throw new InvalidParamException("user.getFirstName()");
		if(StringUtils.isEmpty(user.getLastName())) throw new InvalidParamException("user.getLastName()");
		if(StringUtils.isEmpty(user.getFullName())) throw new InvalidParamException("user.getFullName()");
		if(StringUtils.isEmpty(user.getEmail())) throw new InvalidParamException("user.getEmail()");

		if (user.getId() == null) {
			// 신규 생성건인 경우 ID 생성
			user.setId(WiniCom.getUUIDv7());

			userMapper.insertUser(user);
		} else {
			userMapper.updateUser(user);
		}
		return user;
	}

	@Override
	public User modify(User user, UserCommand.UserModifyCommand command) {
		if(StringUtils.isEmpty(command.getFirstName())) throw new InvalidParamException("user.getFirstName()");
		if(StringUtils.isEmpty(command.getLastName())) throw new InvalidParamException("user.getLastName()");
		if(StringUtils.isEmpty(command.getFullName())) throw new InvalidParamException("user.getFullName()");

		Department department = null;

		if (command.getDepartmentId() != null) {
			department = departmentReader.getDepartment(command.getDepartmentId());
		}

		user.setFirstName(command.getFirstName());
		user.setLastName(command.getLastName());
		user.setFullName(command.getFullName());
		user.setPhoneNumber(command.getPhoneNumber());
		user.setEmployeeNo(command.getEmployeeNo());
		user.setDutyName(command.getDutyName());
		user.setDepartment(department);
		if (command.getJoinStatus() != null) {
			user.setJoinStatus(command.getJoinStatus());
		}
		
		userMapper.updateUser(user);		
		return user;
	}
}
