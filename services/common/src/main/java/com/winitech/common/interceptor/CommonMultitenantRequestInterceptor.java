package com.winitech.common.interceptor;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.config.properties.MultitenantProperties;
import com.winitech.common.exception.InvalidTenantException;
import com.winitech.common.exception.UnauthenticatedException;
import com.winitech.common.library.WiniSecurity;
import com.winitech.common.library.core.CurrentTenantHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * com.winitech.common.interceptor
 * └ CommonJwtRequestInterceptor.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonMultitenantRequestInterceptor extends HandlerInterceptorAdapter {
	@Value("${winitech.devmode:false}")
	private boolean isDevmode;

	@Value("${winitech.devmode.login.skip-login:false}")
	private boolean skipLogin;

	@Value("${winitech.devmode.login.skip-login-jwt-token:}")
	private String skipLoginJwtToken;

	@Value("${winitech.devmode.login.skip-login-org-id:}")
	private String skipLoginOrgId;

	private final LoginUserContext loginUserContext;
	
	private final MultitenantProperties multitenantProperties;
	
	private final Map<UUID, String> orgIdToTenantIdMap = new ConcurrentHashMap<>();

	private UUID defaultTenantId;

	@Value("${winitech.multitenant.default-tenant-id}")
	private void setDefaultTenantId(String defaultTenantId) {
		this.defaultTenantId = UUID.fromString(defaultTenantId);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		loginUserContext.clear();
		CurrentTenantHolder.clear();

		boolean isPreflight = "OPTIONS".equals(request.getMethod()) && request.getHeader("Access-Control-Request-Method") != null && request.getHeader("Origin") != null;
		if (isPreflight) {
			// cors preflight 요청은 통과
			return true;
		}

		String authorization = request.getHeader("Authorization");
		String organizationId = request.getHeader("X-Org-Id");
		
		LoginUserContext userContext;

		if (skipLogin && authorization == null && organizationId == null) {
			// 개발시 로그인 스킵할 때 사용
			
			if (! isDevmode) {
				throw new UnauthenticatedException("winitech.devmode.login.skip-login은 winitech.devmode가 true일 때만 사용 가능합니다.");
			}
			
			if (skipLoginJwtToken == null) {
				throw new UnauthenticatedException("skipLoginJwtToken is null");
			}

			if (skipLoginOrgId == null) {
				throw new UnauthenticatedException("skipLoginOrgId is null");
			}

			userContext = WiniSecurity.parseLoginUserContext(skipLoginJwtToken, skipLoginOrgId);
		} else {
			// 일반 로그인 처리
			userContext = WiniSecurity.parseLoginUserContext(request);
		}

		if (multitenantProperties.isMultitenant()) {
			// 멀티테넌트인 경우 처리
			
			if (handler instanceof HandlerMethod) {
				Class<?> controllerClass = ((HandlerMethod) handler).getBeanType();
				if (controllerClass.isAnnotationPresent(ForceDefaultTenant.class)) {
					// @ForceDefaultTenant 어노테이션이 있는 경우 기본 테넌로 강제 처리
					// TODO : 기본 테넌트로 강제 변경 대신 에러를 띄울지 여부 결정 필요

					if (userContext.getOrganizationId() != null && ! multitenantProperties.getDefaultTenantId().equals(userContext.getOrganizationId())) {
						userContext.setOrganizationId(multitenantProperties.getDefaultTenantId());
						
						log.info("Changed organizationId to default tenant '{}' due to @ForceDefaultTenant", multitenantProperties.getDefaultTenantId());
					}
				}
			}
		}

		loginUserContext.setUserSessionId(userContext.getUserSessionId());
		loginUserContext.setUserId(userContext.getUserId());
		loginUserContext.setOrganizationId(userContext.getOrganizationId());
		loginUserContext.setOrganizationCode(userContext.getOrganizationCode());

		if (! WiniSecurity.isValidOrganizationCodeFormat(loginUserContext.getOrganizationCode())) {
			throw new InvalidTenantException();
		}
		
		CurrentTenantHolder.set(userContext.getOrganizationId(), loginUserContext.getOrganizationCode());
		
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);

		CurrentTenantHolder.clear();

		if (isDevmode) {
			response.addHeader("X-DEVMODE", "true");
		}
	}
}
