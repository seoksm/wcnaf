package com.winitech.system.infrastructure.authorizationGroup;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroup;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupCommand;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* com.winitech.system.interfaces.userGroup
* ㄴ UserGroupStoreImpl.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 4:31
* @see : None
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationGroupStoreImpl implements AuthorizationGroupStore {
    private final AuthorizationGroupRepository authorizationGroupRepository;

    @Override
    public AuthorizationGroup store(AuthorizationGroup authorizationGroup) {
        if(StringUtils.isEmpty(authorizationGroup.getGroupCode())) throw new InvalidParamException("authorizationGroup.getGroupCode()");
        if(StringUtils.isEmpty(authorizationGroup.getGroupName())) throw new InvalidParamException("authorizationGroup.getGroupCode()");
        if(authorizationGroup.getStatus() == null) throw new InvalidParamException("authorizationGroup.getStatus()");
        if(authorizationGroup.getAdminStatus() == null) throw new InvalidParamException("authorizationGroup.getAdminStatus()");

        return authorizationGroupRepository.save(authorizationGroup);
    }

    @Override
    public AuthorizationGroup modify(AuthorizationGroup authorizationGroup, AuthorizationGroupCommand command) {
        if(StringUtils.isEmpty(command.getGroupCode())) throw new InvalidParamException("authorizationGroup.getGroupCode()");
        if(StringUtils.isEmpty(command.getGroupName())) throw new InvalidParamException("authorizationGroup.getGroupCode()");
        if(command.getStatus() == null) throw new InvalidParamException("authorizationGroup.getStatus()");
        if(command.getAdminStatus() == null) throw new InvalidParamException("authorizationGroup.getAdminStatus()");

        authorizationGroup.setGroupCode(command.getGroupCode());
        authorizationGroup.setGroupName(command.getGroupName());
        authorizationGroup.setRemark(command.getRemark());
        authorizationGroup.setStatus(command.getStatus());
        authorizationGroup.setAdminStatus(command.getAdminStatus());
        authorizationGroup.setParentAuthorizationGroup(command.getParentAuthorizationGroupId() == null ? null : authorizationGroupRepository.getById(command.getParentAuthorizationGroupId()));
        
        return authorizationGroupRepository.save(authorizationGroup);
    }
}
