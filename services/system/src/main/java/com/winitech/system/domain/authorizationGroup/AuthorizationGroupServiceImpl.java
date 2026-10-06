package com.winitech.system.domain.authorizationGroup;

import java.util.*;
import java.util.stream.Collectors;

import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.library.core.CircularReferenceDetector;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthorizationGroupServiceImpl extends EgovAbstractServiceImpl implements AuthorizationGroupService{
    private final AuthorizationGroupStore authorizationGroupStore;
    private final AuthorizationGroupReader authorizationGroupReader;

    @Override
    public AuthorizationGroupInfo registerAuthorizationGroup(AuthorizationGroupCommand authorizationGroupCommand) {
        String groupCode = authorizationGroupCommand.getGroupCode();
        
        if (groupCode != null) {
            groupCode = groupCode.toUpperCase();
        }
        
        if (authorizationGroupReader.existAuthorizationGroupByGroupCodeAndExcludingSelf(groupCode, UUID.randomUUID())) {
            throw new IllegalStatusException("이미 사용중인 그룹 코드입니다.");
        }
        
    	AuthorizationGroup initAuthorizationGroup = authorizationGroupCommand.toEntity(authorizationGroupReader);
    	AuthorizationGroup authorizationGroup = authorizationGroupStore.store(initAuthorizationGroup);
        return new AuthorizationGroupInfo(authorizationGroup);
    }

    @Override
    public AuthorizationGroupInfo modifyAuthorizationGroup(UUID id, AuthorizationGroupCommand authorizationGroupCommand){
        String groupCode = authorizationGroupCommand.getGroupCode();

        if (groupCode != null) {
            groupCode = groupCode.toUpperCase();
        }

        if (authorizationGroupReader.existAuthorizationGroupByGroupCodeAndExcludingSelf(groupCode, id)) {
            throw new IllegalStatusException("이미 사용중인 그룹 코드입니다.");
        }
        
        AuthorizationGroup authorizationGroup = authorizationGroupReader.getAuthorizationGroup(id);
    	AuthorizationGroup userGroup = authorizationGroupStore.modify(authorizationGroup, authorizationGroupCommand);

        // 순환 참조 확인을 위한 데이터 조회

        Map<UUID, Set<UUID>> childrenMap = new HashMap<>();

        authorizationGroupReader.getAllAuthorizationGroup().forEach(authGroup -> {
            if (authGroup.getParentAuthorizationGroup() != null) {
                Set<UUID> children = childrenMap.get(authGroup.getParentAuthorizationGroup().getId());

                if (children == null) {
                    children = new HashSet<>();
                    childrenMap.put(authGroup.getParentAuthorizationGroup().getId(), children);
                }

                children.add(authGroup.getId());
            }
        });

        // 순환 참조 확인
        UUID circularReferencedNodeId = CircularReferenceDetector.findFirstCyclicNode(childrenMap);
        if (circularReferencedNodeId != null) {
            log.error("A circular reference was detected. (authorizationId : {})", circularReferencedNodeId);
            throw new IllegalStatusException("A circular reference was detected.");
        }

        return new AuthorizationGroupInfo(userGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorizationGroupInfo searchAuthorizationGroup(UUID id) {
    	AuthorizationGroup userGroup = authorizationGroupReader.getAuthorizationGroup(id);
        return new AuthorizationGroupInfo(userGroup);
    }

    @Override
    public AuthorizationGroupInfo searchAuthorizationGroupByAuthorizationGroupCode(String groupCode) {
        if (groupCode != null) {
            groupCode = groupCode.toUpperCase();
        }

    	AuthorizationGroup userGroup = authorizationGroupReader.getAuthorizationGroupByGroupCode(groupCode);
        return new AuthorizationGroupInfo(userGroup);
    }

    @Override
    @Transactional
    public void removeAuthorizationGroup(UUID id) {
        AuthorizationGroup authorizationGroup = authorizationGroupReader.getAuthorizationGroup(id);
        
        if (authorizationGroup.getChildrenAuthorizationGroup() != null && authorizationGroup.getChildrenAuthorizationGroup().size() > 0) {
            throw new IllegalStatusException("하위 권한 그룹이 존재합니다. 먼저 삭제해주세요.");
        }
        
        authorizationGroup.disable();
        authorizationGroupStore.store(authorizationGroup);
    }

    @Override
    public List<AuthorizationGroupInfo> getAllAuthorizationGroup() {
        List<AuthorizationGroup> userGroupList= authorizationGroupReader.getAllAuthorizationGroup();
        return userGroupList.stream().map(AuthorizationGroupInfo::new).collect(Collectors.toList());
    }
    
    @Override
    public List<AuthorizationGroupInfo.AuthorizationGroupTreeInfo> getAllAuthorizationGroupTree() {
        List<AuthorizationGroup> userGroupList= authorizationGroupReader.getAllAuthorizationGroup();
        return userGroupList.stream()
                .filter(userGroup -> userGroup.getParentAuthorizationGroup() == null)
                .map(AuthorizationGroupInfo.AuthorizationGroupTreeInfo::new)
                .collect(Collectors.toList());
    }
}
