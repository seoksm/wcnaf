package com.winitech.common.application.common;

import com.winitech.common.domain.common.CommonAuthorizationGroupUserCommand;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.application.common
 * └ CommonAuthorizationGroupUserFacade.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-25 09:40
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonAuthorizationGroupUserFacade {
	private final CommonAuthorizationGroupUserService commonAuthorizationGroupUserService;
	
	public void syncAll(List<UUID> authorizationGroupIdList, List<CommonAuthorizationGroupUserCommand> commandList) {
		commonAuthorizationGroupUserService.syncAll(authorizationGroupIdList, commandList);
	}
}
