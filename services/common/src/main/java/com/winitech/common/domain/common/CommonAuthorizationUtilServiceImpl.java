package com.winitech.common.domain.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationUtilServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-26 14:07
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("${winitech.cdc.common.common-authorization.use-common-entity:true}")
public class CommonAuthorizationUtilServiceImpl extends EgovAbstractServiceImpl implements CommonAuthorizationUtilService {
	private final CommonAuthorizationUtilReader commonAuthorizationUtilReader;
	
	@Override
	public boolean hasMenuPermission(UUID userId, UUID menuId, String actionType, String authType, String canonicalizedUri) {
		// 로그인 되어있으면 MEMBER 그룹권한, 로그인 안되어있으면 GUEST 그룹권한 추가 부여
		String extraGroupCode = userId == null ? "GUEST" : "MEMBER";

		List<CommonMenuAction> actionList = commonAuthorizationUtilReader.getActionListByMenuId(userId, extraGroupCode, menuId, actionType, authType);

		List<String> actionUriList = actionList
				.stream()
				.map(CommonMenuAction::getUri)
				.collect(Collectors.toList());

		return checkPermission(actionType, canonicalizedUri, actionUriList);
	}

	@Override
	public boolean hasProgramPermission(UUID userId, String programCode, String actionType, String authType, String canonicalizedUri) {
		// 로그인 되어있으면 MEMBER 그룹권한, 로그인 안되어있으면 GUEST 그룹권한 추가 부여
		String extraGroupCode = userId == null ? "GUEST" : "MEMBER";

		List<CommonMenuAction> actionList = commonAuthorizationUtilReader.getActionListByProgramCode(userId, extraGroupCode, programCode, actionType, authType);

		List<String> actionUriList = actionList
				.stream()
				.map(CommonMenuAction::getUri)
				.collect(Collectors.toList());

		return checkPermission(actionType, canonicalizedUri, actionUriList);
	}

	private static boolean checkPermission(String actionType, String canonicalizedUri, List<String> actionUriList) {
		if ("QUERYID".equals(actionType)) {
			for (String actionUri : actionUriList) {
				String uri = actionUri.trim();

				if (uri.equals(canonicalizedUri)) {
					return true;
				}
			}
		} else {
			PathMatcher pathMatcher = new AntPathMatcher();

			for (String actionUri : actionUriList) {
				String uri = actionUri.trim();

				if (uri.startsWith("/")) {
					uri = uri.substring(1);
				}

				if (uri.endsWith("/")) {
					uri = uri.substring(0, uri.length() - 1);
				}

				if (pathMatcher.match(uri, canonicalizedUri)) {
					return true;
				}
			}
		}

		return false;
	}
}
