package com.winitech.apiGateway.common.filter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.winitech.apiGateway.common.config.ApiGatewayConstants;
import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlockService;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.UnauthenticatedException;
import com.winitech.common.library.WiniSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * com.winitech.apiGateway.common.filter
 * └ AuthenticationFilter.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-01 12:35
 **/
@Slf4j
@Component
@Order(-1)
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter {
    @Value("${winitech.devmode:false}")
    private boolean isDevmode;

    // CommonMultitenantRequestInterceptor와 동일한 패턴 - winitech.devmode만으로는 우회되지 않고
    // skip-login을 별도로 true로 켜야 하며, 토큰 자체는 소스에 두지 않고 설정으로만 주입한다.
    @Value("${winitech.devmode.login.skip-login:false}")
    private boolean skipLogin;

    @Value("${winitech.devmode.login.skip-login-jwt-token:}")
    private String skipLoginJwtToken;

    @Value("${winitech.devmode.login.skip-login-org-id:}")
    private String skipLoginOrgId;

	private final UserSessionBlockService userSessionBlockService;
	
	// 연속된 액세스토큰 차단 조회를 방지하기 위한 캐시
	private final Cache<UUID, Boolean> userSessionBlockExistCache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterWrite(10, TimeUnit.SECONDS)
			.build();
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		RequestPath path = exchange.getRequest().getPath();
		if (path != null) {
			for (String excludedPath : ApiGatewayConstants.EXCLUDE_PATHS) {
				PathMatcher pathMatcher = new AntPathMatcher();
				if (pathMatcher.match(excludedPath, path.value())) {
					log.info("Skipping authentication for path: {}", excludedPath);
					return chain.filter(exchange);
				}
			}
		}
		
		String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
		String organizationHeader = exchange.getRequest().getHeaders().getFirst("X-Org-Id");

        if (skipLogin && authorizationHeader == null) {
            if (!isDevmode) {
                log.error("Authentication failed: winitech.devmode.login.skip-login은 winitech.devmode가 true일 때만 사용 가능합니다.");
                return Mono.error(new UnauthenticatedException());
            }

            if (skipLoginJwtToken.isEmpty() || skipLoginOrgId.isEmpty()) {
                log.error("Authentication failed: winitech.devmode.login.skip-login-jwt-token/skip-login-org-id가 설정되지 않았습니다.");
                return Mono.error(new UnauthenticatedException());
            }

            authorizationHeader = skipLoginJwtToken;
            organizationHeader = skipLoginOrgId;
            exchange.getRequest().mutate().header("Authorization", authorizationHeader).build();
            exchange.getRequest().mutate().header("X-Org-Id", organizationHeader).build();
        }

		LoginUserContext loginUserContext = WiniSecurity.parseLoginUserContext(authorizationHeader, organizationHeader);

		if (loginUserContext == null) {
			log.error("Authentication failed: Invalid token");
			return Mono.error(new UnauthenticatedException());
		}

		// 로그아웃 등으로 인증이 만료되었는지 여부 확인
		Mono<Void> error = checkUserSessionIdBlocked(loginUserContext);
		
		if (error != null) return error;

		exchange.getAttributes().put("loginUserContext", loginUserContext);

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
		}));
	}
	
	private Mono<Void> checkUserSessionIdBlocked(LoginUserContext loginUserContext) {
		// 연속된 액세스토큰 차단 조회를 방지하기 위한 캐시
		// 동시성이 크게 중요하지 않기 때문에 naive하게 구현
		Boolean isBlocked = userSessionBlockExistCache.getIfPresent(loginUserContext.getUserSessionId());

		if (isBlocked == null) {
			// 캐시에 토큰 차단 정보가 없으면 DB에서 조회

			isBlocked = userSessionBlockService.existUserSessionBlockByUserSessionId(loginUserContext.getUserSessionId());
			userSessionBlockExistCache.put(loginUserContext.getUserSessionId(), isBlocked);
		}

		if (isBlocked) {
			log.info("Authentication failed: Token is blocked");
			return Mono.error(new UnauthenticatedException());
		}
		
		return null;
	}
}
