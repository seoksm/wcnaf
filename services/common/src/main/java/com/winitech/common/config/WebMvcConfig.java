package com.winitech.common.config;

import com.winitech.common.interceptor.CommonAuthorizationRequestInterceptor;
import com.winitech.common.interceptor.CommonHttpRequestInterceptor;
import com.winitech.common.interceptor.CommonMultitenantFileRequestInterceptor;
import com.winitech.common.interceptor.CommonMultitenantRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.Arrays;

/**
 * com.winitech.system.common.config
 * └ WebMvcConfig.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("'${spring.main.web-application-type:servlet}'.toUpperCase() != 'REACTIVE'")
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired private Environment env;

	private final CommonMultitenantRequestInterceptor commonMultitenantRequestInterceptor;
	
	private final CommonMultitenantFileRequestInterceptor commonMultitenantFileRequestInterceptor;

	private final CommonHttpRequestInterceptor commonHttpRequestInterceptor;
	
	private final CommonAuthorizationRequestInterceptor commonAuthorizationRequestInterceptor;
	
	//private final RestapiEncryptionFilter restapiEncryptionFilter;

	@Value("${winitech.security.cors.origins:*}")
	private String[] corsOriginPattern;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(commonHttpRequestInterceptor)
				.addPathPatterns("/**");

		if (!Arrays.asList(env.getActiveProfiles()).contains("test")) {
			registry.addInterceptor(commonMultitenantRequestInterceptor)
					.addPathPatterns("/api/v1/basic/**")
					.order(Ordered.HIGHEST_PRECEDENCE);
		}

		
		// 파일 다운로드와 미리보기의 멀티테넌트를 위해 별도 등록
		// 파일 다운로드와 미리보기는 cookie나 header에 orgId를 넣지 않고 호출하는데
		// 경로에 테넌트 ID가 포함되어 있어 이를 처리하기 위함 (/테넌트ID/api/v1/system/commonFile/preview/...)
//		registry.addInterceptor(commonMultitenantFileRequestInterceptor)
//				.addPathPatterns(
//						"/api/v1/*/commonFile/download/**",
//						"/api/v1/*/commonFile/preview/**"
//				)
//				.order(Ordered.HIGHEST_PRECEDENCE);
		
		registry.addInterceptor(commonAuthorizationRequestInterceptor)
				.addPathPatterns("/api/**")
				.excludePathPatterns(
						"/api/v1/system/user/login", 
						"/api/v1/system/user/logout", 
						"/api/v1/system/user/join", 
						"/api/v1/system/user/refreshToken", 
						"/api/v1/system/commonSecurity/startRestApiEnc",	// 암호화 시작 API 
						
						// 첨부파일 다운로드와 미리보기는 파일 ID를 서명하여 별도로 처리
						"/api/v1/*/commonFile/download/**", 
						"/api/v1/*/commonFile/preview/**", 
				
						// 이미지 AI 업로드는 임시로 로그인 해제
						"/api/v1/collection/rtu/transfer/water-level-image",
						
						// sms 수신 api webhook은 로그인 해제
						"/api/v1/sms/twilio/webHook",
						
						// 개발용 swagger 페이지는 권한체크 X
						"/api/v1/*/swagger-ui/**", 
						"/api/v1/*/swagger-resources/**", 
						"/api/v1/*/v2/api-docs/**", 
						"/api/v1/*/v3/api-docs/**");
	}


	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
		config.setAllowedOriginPatterns(Arrays.asList(corsOriginPattern));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
		config.setAllowedHeaders(Arrays.asList("Authorization", "X-Org-Id", "Content-Type", "X-Restapi-Enc", "X-Menu-Id", "X-Program-Id", "X-Enc-Iv", "X-Enc-Key"));
		config.setExposedHeaders(Arrays.asList("Content-Disposition"));
		config.setAllowCredentials(true);
		config.setMaxAge(Duration.ofMinutes(60));
		source.registerCorsConfiguration("/**", config);
		
		registrationBean.setFilter(new CorsFilter(source));
		registrationBean.addUrlPatterns("/api/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}
}
