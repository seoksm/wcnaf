package com.winitech.common.interceptor;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.config.properties.MultitenantProperties;
import com.winitech.common.exception.InvalidTenantException;
import com.winitech.common.exception.UnauthenticatedException;
import com.winitech.common.library.WiniFile;
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
 * <pre>
 * com.winitech.common.interceptor
 * └ CommonMultitenantFileRequestInterceptor.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-09-03 13:05
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonMultitenantFileRequestInterceptor extends HandlerInterceptorAdapter {
	private final LoginUserContext loginUserContext;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 파일 다운로드/미리보기 요청은 인증없이, organizationId 없이 tenantId만 있으면 접근 허용

		loginUserContext.clear();
		CurrentTenantHolder.clear();

		boolean isPreflight = "OPTIONS".equals(request.getMethod()) && request.getHeader("Access-Control-Request-Method") != null && request.getHeader("Origin") != null;
		if (isPreflight) {
			// cors preflight 요청은 통과
			return true;
		}

		String tenantId = request.getHeader("x-tenant-id");
		
		// 단, 파일 URL의 무결성은 검증함
		WiniFile.checkSignedFileId(WiniFile.getFileName(request.getRequestURI()));
		
		if (! WiniSecurity.isValidOrganizationCodeFormat(tenantId)) {
			throw new InvalidTenantException();
		}

		CurrentTenantHolder.set(null, tenantId);

		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);

		CurrentTenantHolder.clear();
	}
}
