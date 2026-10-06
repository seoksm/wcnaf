package com.winitech.system.infrastructure.user;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.library.WiniConst;
import com.winitech.system.domain.department.DepartmentReader;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserCommand;
import com.winitech.system.domain.user.UserStore;
import com.winitech.system.domain.department.Department;
import com.winitech.system.domain.department.DepartmentInfo;
import com.winitech.system.domain.department.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
* com.winitech.user.infrastructure
* ㄴ UserStoreImpl.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 5:12
* @see : None
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class UserStoreImpl implements UserStore {
    private final UserRepository userRepository;
    private final DepartmentReader departmentReader;

    @Override
    public User store(User user) {
        if(StringUtils.isEmpty(user.getFirstName())) throw new InvalidParamException("user.getFirstName()");
        if(StringUtils.isEmpty(user.getLastName())) throw new InvalidParamException("user.getLastName()");
        if(StringUtils.isEmpty(user.getFullName())) throw new InvalidParamException("user.getFullName()");
        if(StringUtils.isEmpty(user.getEmail())) throw new InvalidParamException("user.getEmail()");
        return userRepository.save(user);
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
        return userRepository.save(user);
    }
}
