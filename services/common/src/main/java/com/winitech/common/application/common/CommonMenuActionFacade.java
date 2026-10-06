package com.winitech.common.application.common;

import com.winitech.common.domain.common.CommonMenuAction;
import com.winitech.common.domain.common.CommonMenuActionCommand;
import com.winitech.common.domain.common.CommonMenuActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.application.common
 * └ CommonMenuActionFacade.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-25 11:05
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonMenuActionFacade {
	private final CommonMenuActionService commonMenuActionService; 
	
	public void syncAll(List<UUID> programIdList, List<CommonMenuActionCommand> commandList) {
		commonMenuActionService.syncAll(programIdList, commandList);
	}
}
