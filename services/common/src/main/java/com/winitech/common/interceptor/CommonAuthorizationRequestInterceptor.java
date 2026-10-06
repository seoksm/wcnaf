package com.winitech.common.interceptor;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.domain.common.CommonAuthorizationUtilService;
import com.winitech.common.exception.UnauthorizedException;
import com.winitech.common.library.WiniSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * com.winitech.common.interceptor
 * └ CommonAuthorizationRequestInterceptor.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-26 13:56
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonAuthorizationRequestInterceptor extends HandlerInterceptorAdapter {
	@Value("${winitech.devmode:false}")
	private boolean isDevMode;
	
	@Value("${winitech.security.authorization.mode:on}")
	private void setAuthorizationMode(String mode) {
		skipPermissionCheck = "off".equalsIgnoreCase(mode);
		skipThrowException = "silent".equalsIgnoreCase(mode);
		isApiGatewayMode = "api-gateway".equalsIgnoreCase(mode);
	}

	@Value("${winitech.security.api-gateway.magic-header}")
	private String apiGatewayMagicHeader;

	@Value("${winitech.security.api-gateway.magic-header-secret:}")
	private String apiGatewayMagicHeaderSecret;

	private static final int MIN_MAGIC_HEADER_SECRET_BYTES = 32;

	@PostConstruct
	private void validateMagicHeaderSecret() {
		if (isApiGatewayMode && apiGatewayMagicHeaderSecret.getBytes(StandardCharsets.UTF_8).length < MIN_MAGIC_HEADER_SECRET_BYTES) {
			throw new IllegalStateException(
					"winitech.security.api-gateway.magic-header-secret(환경변수 WINITECH_API_GATEWAY_MAGIC_HEADER_SECRET)이 "
							+ "설정되지 않았거나 너무 짧습니다. 최소 " + MIN_MAGIC_HEADER_SECRET_BYTES + "바이트 이상의 무작위 값이 필요합니다.");
		}
	}

	private final LoginUserContext loginUserContext;
	private final CommonAuthorizationUtilService commonAuthorizationUtilService;

	private boolean skipThrowException = false;
	private boolean skipPermissionCheck = false;
	private boolean isApiGatewayMode = false; 
	// private final Pattern simpleCrudPathPattern = Pattern.compile("/([^/]+)/crud/([^/]+)/([^/]+)");
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (skipPermissionCheck) {
			return super.preHandle(request, response, handler);
		}
		
		if (isApiGatewayMode) {
			// API Gateway 모드에서만 사용
			String magicHeader = request.getHeader(apiGatewayMagicHeader);
			String requestId = request.getHeader(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
			
			if (magicHeader != null && requestId != null) {
				String calculatedMagicHeader = WiniSecurity.hashMd5(apiGatewayMagicHeaderSecret + requestId);
				
				if (magicHeader.equals(calculatedMagicHeader)) {
					// API Gateway에서 magic header가 일치하는 경우 권한 체크를 하지 않음
					return super.preHandle(request, response, handler);
				}
			}
		}
		
		String rawMenuId = request.getHeader("x-menu-id");
		String uri = request.getRequestURI();
		boolean useMenuIdForPermission;
	
		if (isDevMode && (rawMenuId == null || rawMenuId.isEmpty() || "DEV".equals(rawMenuId))) {
			// 개발모드에서 메뉴ID가 없거나 x-menu-id가 DEV인 경우 권한 체크를 하지 않음
			return super.preHandle(request, response, handler);
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
				switch (rawMenuId) {
					case "Main":
						useMenuIdForPermission = false;
						programCode = "MAIN";
						break;
					default:
						useMenuIdForPermission = true;
						menuId = UUID.fromString(rawMenuId);
						break;
				}
			}
		} catch (IllegalArgumentException ex) {
			if (skipThrowException) {
				log.warn("권한이 없습니다. (1) (menuId: {}, actionType: {}, canonicalizedUri: {})", menuId, actionType, canonicalizedUri);
				return super.preHandle(request, response, handler);
			} else {
				throw new UnauthorizedException("권한이 없습니다. (1)");
			}
		}

		authType = getAuthType(request);

		boolean hasPermission;
		
		if (useMenuIdForPermission) {
			// 메뉴ID를 사용하여 권한을 확인
			log.info("menuId: {}, actionType: {}, authType: {}, canonicalizedUri: {}", menuId, actionType, authType, canonicalizedUri);
			
			hasPermission = commonAuthorizationUtilService.hasMenuPermission(loginUserContext.getUserId(), menuId, actionType, authType, canonicalizedUri);
		} else {
			// ProgramID를 사용하여 권한 확인
			log.info("programCode: {}, actionType: {}, authType: {}, canonicalizedUri: {}", programCode, actionType, authType, canonicalizedUri);

			hasPermission = commonAuthorizationUtilService.hasProgramPermission(loginUserContext.getUserId(), programCode, actionType, authType, canonicalizedUri);
		}
		
		if (hasPermission) {
			return super.preHandle(request, response, handler);
		}

		if (skipThrowException) {
			log.warn("권한이 없습니다. (2) (menuId: {}, programCode: {}, actionType: {}, authType: {}, canonicalizedUri: {}", menuId, programCode, actionType, authType, canonicalizedUri);
			return super.preHandle(request, response, handler);
		} else {
			throw new UnauthorizedException("권한이 없습니다. (2)");
		}
	}

	private static String getAuthType(HttpServletRequest request) {
		String authType;
		switch (request.getMethod()) {
			case "GET":
				authType = "SELECT";
				break;
			case "POST":
				authType = "INSERT";
				break;
			case "PATCH":
				authType = "UPDATE";
				break;
			case "DELETE":
				authType = "DELETE";
				break;
			default:
				if (request.getParameter("AUTHTYPE") == null) {
					throw new UnauthorizedException("권한이 없습니다. (3)");
				}
				
				switch (request.getParameter("AUTHTYPE")) {
					case "PRINT":
					case "DOWN":
						authType = request.getParameter("AUTHTYPE");
						break;
					default:
						throw new UnauthorizedException("권한이 없습니다. (4)");
				}
				break;
		}
		return authType;
	}
}
