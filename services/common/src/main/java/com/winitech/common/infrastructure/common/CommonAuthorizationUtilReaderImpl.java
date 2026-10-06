package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonAuthorizationUtilReader;
import com.winitech.common.domain.common.CommonMenuAction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationUtilReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-26 14:09
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonAuthorizationUtilReaderImpl implements CommonAuthorizationUtilReader {
	private final CommonAuthorizationUtilRepository commonAuthorizationUtilRepository;

	@Override
	public List<CommonMenuAction> getActionListByMenuId(UUID userId, String extraGroupCode, UUID menuId, String actionType, String authType) {
		List<CommonMenuAction> menuActionList = commonAuthorizationUtilRepository.findActionListByMenuId(userId, extraGroupCode, menuId, actionType, authType);

		return menuActionList;
	}

	@Override
	public List<CommonMenuAction> getActionListByProgramCode(UUID userId, String extraGroupCode, String programCode, String actionType, String authType) {
		List<CommonMenuAction> menuActionList = commonAuthorizationUtilRepository.findActionListByProgramCode(userId, extraGroupCode, programCode, actionType, authType);

		return menuActionList;
	}
}
