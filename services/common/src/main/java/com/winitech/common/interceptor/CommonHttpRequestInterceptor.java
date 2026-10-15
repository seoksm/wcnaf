package com.winitech.common.interceptor;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.library.WiniCom;
import com.winitech.common.library.WiniSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonHttpRequestInterceptor extends HandlerInterceptorAdapter {
    public static final String HEADER_REQUEST_UUID_KEY = "x-request-id";

    /**
     * API Gateway 매직헤더 인증 전용 nonce 헤더명. x-request-id는 클라이언트가 임의로 지정할 수
     * 있어(분산 트레이싱 목적상 의도된 동작) 인증용 MAC의 입력으로 쓰기에 부적합하므로, 게이트웨이가
     * 매번 자체적으로 생성하는 이 nonce만 매직헤더 계산에 사용한다.
     */
    public static final String HEADER_MAGIC_NONCE_KEY = "x-magic-nonce";

    private final LoginUserContext loginUserContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = request.getHeader(HEADER_REQUEST_UUID_KEY);

        if (StringUtils.isEmpty(requestId)) {
            requestId = WiniCom.generateRequestId();
        }

        if (request.getHeader("Authorization") != null) {
            loginUserContext.clear();
            LoginUserContext userContext = WiniSecurity.parseLoginUserContext(request);
            loginUserContext.setUserSessionId(userContext.getUserSessionId());
            loginUserContext.setUserId(userContext.getUserId());
            loginUserContext.setOrganizationId(userContext.getOrganizationId());
            loginUserContext.setOrganizationCode(userContext.getOrganizationCode());
            loginUserContext.setUserIp(request.getRemoteAddr());
            loginUserContext.setAuthGroupCode(userContext.getAuthGroupCode());
            loginUserContext.setAccessTokenExpiredAt(userContext.getAccessTokenExpiredAt());
        }

        MDC.put(HEADER_REQUEST_UUID_KEY, requestId);

        request.setAttribute("requestId", requestId);
        response.addHeader(HEADER_REQUEST_UUID_KEY, requestId.replaceAll("[\\r\\n]", ""));

        return true;
    }
}
