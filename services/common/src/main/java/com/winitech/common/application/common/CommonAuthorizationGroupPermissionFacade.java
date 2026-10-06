package com.winitech.common.application.common;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionCommand;
import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.application.common
 * └ CommonAuthorizationGroupPermissionFacade.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-25 11:05
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonAuthorizationGroupPermissionFacade {
	private final CommonAuthorizationGroupPermissionService commonAuthorizationGroupPermissionService;
	
	public void syncAll(List<UUID> authorizationGroupIdList, List<CommonAuthorizationGroupPermissionCommand> commandList) {
		commonAuthorizationGroupPermissionService.syncAll(authorizationGroupIdList, commandList);
	}
}
