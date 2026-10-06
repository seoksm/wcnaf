package com.winitech.system.infrastructure.authorizationGroup;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroup;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* com.winitech.system.interfaces.userGroup
* ㄴ UserGroupReaderImpl.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 4:31
* @see : None
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationGroupReaderImpl implements AuthorizationGroupReader {

    private final AuthorizationGroupRepository authorizationGroupRepository;

    @Override
    public AuthorizationGroup getAuthorizationGroup(UUID id) {
        return authorizationGroupRepository.findByIdAndSystemStatus(id, AuthorizationGroup.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public AuthorizationGroup getAuthorizationGroupByGroupCode(String groupCode) {
        return authorizationGroupRepository.findByGroupCodeAndSystemStatus(groupCode, AuthorizationGroup.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<AuthorizationGroup> getAllAuthorizationGroup() {
        return authorizationGroupRepository.findAllBySystemStatusOrderByGroupName(AuthorizationGroup.SystemStatus.ENABLE);
    }
    
    @Override
    public Boolean existAuthorizationGroupByGroupCodeAndExcludingSelf(String groupCode, UUID id) {
        return authorizationGroupRepository.existsByGroupCodeAndIdNotAndSystemStatus(groupCode, id, AuthorizationGroup.SystemStatus.ENABLE);
    }
}
