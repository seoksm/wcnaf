package com.winitech.apiGateway.common.filter;

import com.google.common.base.Functions;
import com.winitech.apiGateway.common.config.ApiGatewayConfig;
import com.winitech.apiGateway.common.config.ApiGatewayConstants;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.domain.common.*;
import com.winitech.apiGateway.common.event.AuthorizationChangeEvent;
import com.winitech.common.exception.UnauthorizedException;
import com.winitech.common.library.WiniDebouncer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationListener;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 인가를 처리하는 필터.
 * 인가 정보는 DB에서 한꺼번에 불러와서 Map에 저장하고 사용합니다.
 * 그 이유는 첫번째로 매번 DB에서 불러오는것은 성능상 좋지 않기 때문입니다.
 * 두번째로는 Reactive한 비동기 방식을 위해 별도로 R2DBC를 사용하지 않기 위해 기존 JPA를 활용하기 때문에
 * 더욱 성능상 (구조상) 좋지 않기 때문입니다.
 * 
 * 리로드시와 권한체크시에 동시성에 의한 race condition이 예상되는 바,
 * ReentrantReadWriteLock을 사용하여 읽기와 쓰기를 lock 걸려고 생각해보았으나
 * non-Blocking IO의 특성상 lock을 거는것이 부담스럽기 때문에 (쓰레드 모델과는 달리 lock을 걸면 성능이 매우 떨어짐)
 * 모든 인증 정보를 하나의 Map에 넣고 immutable하게하고 lock 없이 swap하는것으로 처리 하였습니다.
 * <pre>
 * com.winitech.apiGateway.common.filter
 * └ AuthorizationFilter.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-01 12:36
 **/
@Slf4j
@Component
public class AuthorizationFilter implements GlobalFilter, ApplicationListener<AuthorizationChangeEvent> {
	@Value("${winitech.devmode:false}")
	private boolean isDevMode;

	@Value("${winitech.security.authorization.mode:on}")
	private void setAuthorizationMode(String mode) {
		skipPermissionCheck = "off".equalsIgnoreCase(mode);
		skipThrowException = "silent".equalsIgnoreCase(mode);
	}

	private final CommonAuthorizationGroupUserService commonAuthorizationGroupUserService;
	private final CommonAuthorizationGroupPermissionService commonAuthorizationGroupPermissionService;
	private final CommonMenuActionService commonMenuActionService;
	private final CommonAuthorizationUtilService commonAuthorizationUtilService;

	private boolean skipThrowException = false;
	private boolean skipPermissionCheck = false;
	
	private AuthorizationFilterData authorizationFilterData = new AuthorizationFilterData();
	
	public AuthorizationFilter(
			CommonAuthorizationGroupUserService commonAuthorizationGroupUserService,
			CommonAuthorizationGroupPermissionService commonAuthorizationGroupPermissionService,
			CommonMenuActionService commonMenuActionService,
			CommonAuthorizationUtilService commonAuthorizationUtilService
	) {
		this.commonAuthorizationGroupUserService = commonAuthorizationGroupUserService;
		this.commonAuthorizationGroupPermissionService = commonAuthorizationGroupPermissionService;
		this.commonMenuActionService = commonMenuActionService;
		this.commonAuthorizationUtilService = commonAuthorizationUtilService;

		reloadPermissionData();
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		RequestPath path = exchange.getRequest().getPath();
		if (path != null) {
			for (String excludedPath : ApiGatewayConstants.EXCLUDE_PATHS) {
				PathMatcher pathMatcher = new AntPathMatcher();
				if (pathMatcher.matchStart(excludedPath, path.value())) {
					log.info("Skipping authentication for path: {}", excludedPath);
					return chain.filter(exchange);
				}
			}
		}

		if (skipPermissionCheck) {
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			}));
		}

		String rawMenuId = exchange.getRequest().getHeaders().getFirst("X-Menu-Id");
		String uri = exchange.getRequest().getURI().getPath();
		boolean useMenuIdForPermission;

		if (isDevMode && (rawMenuId == null || rawMenuId.isEmpty() || "DEV".equals(rawMenuId))) {
			// 개발모드에서 메뉴ID가 없거나 x-menu-id가 DEV인 경우 권한 체크를 하지 않음
			return chain.filter(exchange);
		}

		UUID menuId = null;
		String programCode = null;
		String actionType = "RESTAPI";
		String authType;
		String canonicalizedUri = uri;

		if (uri.endsWith("/")) {
			canonicalizedUri = uri.substring(0, uri.length() - 1);
		}

		canonicalizedUri = canonicalizedUri.substring(uri.indexOf("/", "/api/".length()) + 1);

		// /api/v1/{serviceName}/crud/{mapperName}/{queryId} 형태의 SimpleCRUD는 queryId로 권한을 처리하지 않고
		// 자체를 API로 보고 처리
		// queryId로 권한 체크하는것은 CmmWccController 처럼 사용하는 경우에 사용. (해당 usecase는 없음)
		//
		//		if (canonicalizedUri.contains("/crud/")) {
		//			Matcher matcher = simpleCrudPathPattern.matcher(canonicalizedUri);
		//			
		//			if (matcher.matches()) {
		//				String serviceName = matcher.group(1);
		//				String mapperName = matcher.group(2);
		//				String queryId = matcher.group(3);
		//				
		//				actionType = "QUERYID";
		//				canonicalizedUri = queryId;
		//			}
		//		}

		try {
			if (rawMenuId == null) {
				useMenuIdForPermission = false;
				programCode = "DEFAULT";
			} else {
				switch (rawMenuId.toUpperCase()) {
					case "MAIN":
						useMenuIdForPermission = false;
						programCode = "MAIN";
						break;
					case "DEV":
						throw new UnauthorizedException("ApiGateway는 dev페이지를 지원하지 않습니다.");
					default:
						useMenuIdForPermission = true;
						menuId = UUID.fromString(rawMenuId);
						break;
				}
			}
		} catch (UnauthorizedException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new UnauthorizedException("권한이 없습니다. (1)");
		}

		authType = getAuthType(exchange);

		boolean hasPermission;

		UUID userId = null;
		LoginUserContext loginUserContext = exchange.getAttribute("loginUserContext");

		if (loginUserContext != null) {
			userId = loginUserContext.getUserId();
		}

		
		if (useMenuIdForPermission) {
			// 메뉴ID를 사용하여 권한을 확인
			log.info("userId: {}, menuId: {}, actionType: {}, authType: {}, canonicalizedUri: {}", userId, menuId, actionType, authType, canonicalizedUri);

//			hasPermission = commonAuthorizationUtilService.hasMenuPermission(loginUserContext.getUserId(), menuId, actionType, authType, canonicalizedUri);
		} else {
			// ProgramID를 사용하여 권한 확인
			log.info("userId: {}, programCode: {}, actionType: {}, authType: {}, canonicalizedUri: {}", userId, programCode, actionType, authType, canonicalizedUri);

//			hasPermission = commonAuthorizationUtilService.hasProgramPermission(loginUserContext.getUserId(), programCode, actionType, authType, canonicalizedUri);
			
			// 프로그램 코드로 메뉴ID를 찾기
			menuId = authorizationFilterData.getProgramCodeToMenuIdMap().get(programCode);
		}

		hasPermission = hasMenuPermission(userId, menuId, actionType, authType, canonicalizedUri);
		
		if (hasPermission) {
			// 권한이 있는 경우
		} else if (skipThrowException) {
			// 권한이 없지만 무시하는 경우 로그만 남김
			log.warn("권한이 없습니다. (2) (menuId: {}, programCode: {}, actionType: {}, authType: {}, canonicalizedUri: {}", menuId, programCode, actionType, authType, canonicalizedUri);
		} else {
			// 권한이 없고 무시하는 경우가 아니면 에러 처리
			throw new UnauthorizedException("권한이 없습니다. (2)");
		}

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
		}));
	}
	
	private void reloadPermissionData() {
		// 사용자 => 그룹 맵핑
		List<CommonAuthorizationGroupUserInfo> allCommonAuthorizationGroupUser = commonAuthorizationGroupUserService.getAllCommonAuthorizationGroupUser();

		Map<UUID, List<UUID>> newUserGroupListMap = allCommonAuthorizationGroupUser.stream()
				.collect(Collectors.groupingBy(
						CommonAuthorizationGroupUserInfo::getUserId,
						Collectors.mapping(CommonAuthorizationGroupUserInfo::getAuthorizationGroupId, Collectors.toList())
				));

		// 그룹 => 메뉴 => 권한 매핑
		List<CommonAuthorizationGroupPermissionInfo> allCommonAuthorizationGroupPermission = commonAuthorizationGroupPermissionService.getAllCommonAuthorizationGroupPermission();

		Map<UUID, Map<UUID, CommonAuthorizationGroupPermissionInfo>> newGroupMenuPermissionMap = allCommonAuthorizationGroupPermission.stream()
				.collect(Collectors.groupingBy(
						CommonAuthorizationGroupPermissionInfo::getAuthorizationGroupId,
						Collectors.toMap(
								CommonAuthorizationGroupPermissionInfo::getMenuId,
								Functions.identity()
						)
				));

		// 그룹코드 => 그룹 매핑
		Map<String, UUID> newGroupCodeToGroupMap = new HashMap<>();

		newGroupMenuPermissionMap.values().stream().forEach(it -> {
			Iterator<CommonAuthorizationGroupPermissionInfo> iter = it.values().iterator();
			
			if (iter.hasNext()) {
				CommonAuthorizationGroupPermissionInfo permissionInfo = iter.next();

				newGroupCodeToGroupMap.put(permissionInfo.getGroupCode(), permissionInfo.getAuthorizationGroupId());
			}
		});

		// 메뉴 => 액션 매핑
		List<CommonMenuActionInfo> allCommonMenuAction = commonMenuActionService.getAllCommonMenuAction();

		Map<UUID, List<CommonMenuActionInfo>> newMenuActionMap = allCommonMenuAction.stream()
				.collect(Collectors.groupingBy(
						CommonMenuActionInfo::getMenuId,
						Collectors.toList()
				));

		// 프로그램 코드 => 메뉴ID 매핑
		Map<String, UUID> newProgramCodeToMenuIdMap = new HashMap<>();

		newMenuActionMap.values().stream().forEach(it -> {
			newProgramCodeToMenuIdMap.put(it.get(0).getProgramCode(), it.get(0).getMenuId());
		});

		authorizationFilterData = new AuthorizationFilterData(
				newUserGroupListMap, 
				newGroupCodeToGroupMap,
				newGroupMenuPermissionMap,
				newProgramCodeToMenuIdMap,
				newMenuActionMap
		);

		// 마지막 권한데이터 업데이트 시간 조회
		OffsetDateTime lastUpdateAt = getMaxUpdateAt(allCommonAuthorizationGroupUser, allCommonAuthorizationGroupPermission, allCommonMenuAction);

		ApiGatewayConfig.setAuthorizationDataLoadedTime(lastUpdateAt);
	}

	private static OffsetDateTime getMaxUpdateAt(List<CommonAuthorizationGroupUserInfo> allCommonAuthorizationGroupUser, List<CommonAuthorizationGroupPermissionInfo> allCommonAuthorizationGroupPermission, List<CommonMenuActionInfo> allCommonMenuAction) {
		OffsetDateTime maxGroupUserUpdateAt = allCommonAuthorizationGroupUser.stream() 
				.map(CommonAuthorizationGroupUserInfo::getUpdateAt)
				.max(Comparator.nullsFirst(Comparator.naturalOrder()))
				.orElse(null);

		OffsetDateTime maxGroupPermissionUpdateAt = allCommonAuthorizationGroupPermission.stream()
				.map(CommonAuthorizationGroupPermissionInfo::getUpdateAt)
				.max(Comparator.nullsFirst(Comparator.naturalOrder()))
				.orElse(null);

		OffsetDateTime maxMenuActionUpdateAt = allCommonMenuAction.stream() 
				.map(CommonMenuActionInfo::getUpdateAt)
				.max(Comparator.nullsFirst(Comparator.naturalOrder()))
				.orElse(null);

		OffsetDateTime lastUpdateAt = Collections.max(Arrays.asList(
				maxGroupUserUpdateAt,
				maxGroupPermissionUpdateAt,
				maxMenuActionUpdateAt
		), Comparator.nullsFirst(Comparator.naturalOrder()));

		return lastUpdateAt;
	}

	private boolean hasMenuPermission(UUID userId, UUID menuId, String actionType, String authType, String canonicalizedUri) {
		List<UUID> groupIdList = new ArrayList<>();
		
		if (userId == null) {
			// 로그인하지 않은 경우 GUEST 권한 코드 부여
			UUID guestGroupId = authorizationFilterData.getGroupCodeToGroupMap().get("GUEST");
			groupIdList.add(guestGroupId);
		} else {
			// 로그인한 경우 사용자 그룹을 가져옴
			if (authorizationFilterData.getUserGroupListMap().get(userId) != null) {
				groupIdList.addAll(authorizationFilterData.getUserGroupListMap().get(userId));
			}
			
			// 기본적으로 MEMBER 그룹코드의 권한도 부여
			UUID memberGroupId = authorizationFilterData.getGroupCodeToGroupMap().get("MEMBER");
			if (memberGroupId != null) {
				groupIdList.add(memberGroupId);
			}
		}
		
		boolean hasPermission = false;

		// 기능 권한이 있는지 확인
		for (UUID groupId : groupIdList) {
			// 소속된 그룹의 메뉴별 권한 검사
			Map<UUID, CommonAuthorizationGroupPermissionInfo> menuPermissionMap = authorizationFilterData.getGroupMenuPermissionMap().get(groupId);

			if (menuPermissionMap == null) {
                continue;
			}
			
			CommonAuthorizationGroupPermissionInfo commonAuthorizationGroupPermissionInfo = menuPermissionMap.get(menuId);

			if (commonAuthorizationGroupPermissionInfo == null) {
				continue;
			}

			hasPermission = checkAuthTypePermission(commonAuthorizationGroupPermissionInfo, authType);

			if (hasPermission) {
				break;
			}
		}
		
		if (! hasPermission) {
			return false;
		}
		
		// 기능 권한이 있는 경우 액션 권한이 있는지 체크
		List<CommonMenuActionInfo> actionList = authorizationFilterData.getMenuActionMap().get(menuId);
		
		if (actionList == null) {
			return false;
		}

		// URL로 권한 비교
		List<String> actionUrlList = authorizationFilterData.getMenuActionMap().get(menuId) 
				.stream()
				.filter(it -> authType.equals(it.getAuthType()))
				.map(CommonMenuActionInfo::getUri)
				.collect(Collectors.toList());
				
		return checkPermission(actionType, canonicalizedUri, actionUrlList);
	}

	private static boolean checkAuthTypePermission(CommonAuthorizationGroupPermissionInfo commonAuthorizationGroupPermissionInfo, String authType) {
		switch (authType) {
			case "SELECT":
				return commonAuthorizationGroupPermissionInfo.getSelectStatus() == CommonAuthorizationGroupPermission.MenuPermissionStatus.ALLOW;
			case "INSERT":
				return commonAuthorizationGroupPermissionInfo.getInsertStatus() == CommonAuthorizationGroupPermission.MenuPermissionStatus.ALLOW;
			case "UPDATE":
				return commonAuthorizationGroupPermissionInfo.getUpdateStatus() == CommonAuthorizationGroupPermission.MenuPermissionStatus.ALLOW;
			case "DELETE":
				return commonAuthorizationGroupPermissionInfo.getDeleteStatus() == CommonAuthorizationGroupPermission.MenuPermissionStatus.ALLOW;
			case "PRINT":
				return commonAuthorizationGroupPermissionInfo.getPrintStatus() == CommonAuthorizationGroupPermission.MenuPermissionStatus.ALLOW;
			case "DOWN":
				return commonAuthorizationGroupPermissionInfo.getDownStatus() == CommonAuthorizationGroupPermission.MenuPermissionStatus.ALLOW;
			case "MANAGE":
				return commonAuthorizationGroupPermissionInfo.getManageStatus() == CommonAuthorizationGroupPermission.MenuPermissionStatus.ALLOW;
			case "CUSTOM1":
				return commonAuthorizationGroupPermissionInfo.getCustom1Status() == CommonAuthorizationGroupPermission.MenuPermissionStatus.ALLOW;
			case "CUSTOM2":
				return commonAuthorizationGroupPermissionInfo.getCustom2Status() == CommonAuthorizationGroupPermission.MenuPermissionStatus.ALLOW;
			case "CUSTOM3":
				return commonAuthorizationGroupPermissionInfo.getCustom3Status() == CommonAuthorizationGroupPermission.MenuPermissionStatus.ALLOW;
		}
		
		return false;
	}

	private static String getAuthType(ServerWebExchange exchange) {
		String result;
		switch (exchange.getRequest().getMethod()) {
			case GET:
				result = "SELECT";
				break;
			case POST:
				result = "INSERT";
				break;
			case PATCH:
				result = "UPDATE";
				break;
			case DELETE:
				result = "DELETE";
				break;
			default:
				MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
				String authType = queryParams.getFirst("AUTHTYPE");

				if (authType == null) {
					throw new UnauthorizedException("권한이 없습니다. (3)");
				}

				switch (authType) {
					case "PRINT":
					case "DOWN":
						result = authType;
						break;
					default:
						throw new UnauthorizedException("권한이 없습니다. (4)");
				}
				break;
		}
		return result;
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
	
	private WiniDebouncer reloadPermissionDataDebouncer = new WiniDebouncer();

	@Override
	public void onApplicationEvent(AuthorizationChangeEvent event) {
		log.info("권한 데이터 변경 이벤트 수신");

		reloadPermissionDataDebouncer.debounce(() -> {
			log.info("권한 데이터 변경 이벤트 적용");
			reloadPermissionData();
		}, 1000);
	}
}
