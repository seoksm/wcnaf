package com.winitech.system.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 설정
 * <pre>
 * com.winitech.system.common.config
 * └ RestTemplateConfig.java
 *
 * RestTemplate은 Spring에서 제공하는 HTTP 클라이언트로, 외부 API 호출에 사용됩니다.
 * - TossPayments API 호출
 * - 외부 서비스 연동
 *
 * 주요 설정:
 * - Connection Timeout: 외부 서버 연결 시 최대 대기 시간
 * - Read Timeout: 응답 데이터 읽기 최대 대기 시간
 * - HttpComponentsClientHttpRequestFactory: Apache HttpClient 기반 요청 처리
 * </pre>
 *
 * @author 이승국 (CLOUD팀)
 * @since 2025-10-31
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate Bean 생성
     *
     * @return 설정된 RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        // Apache HttpClient 기반 Request Factory 생성
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        // 연결 타임아웃: 외부 서버 연결 시도 최대 5초
        // - 네트워크 지연이나 서버 무응답 시 5초 후 실패 처리
        factory.setConnectTimeout(5000);

        // 읽기 타임아웃: 응답 데이터 읽기 최대 30초
        // - API 처리 시간이 길 경우를 대비하여 30초 설정
        // - TossPayments API는 일반적으로 1~3초 내 응답
        factory.setReadTimeout(30000);

        // Connection Pool 설정 (선택사항)
        // factory.setMaxConnTotal(100);        // 최대 연결 수
        // factory.setMaxConnPerRoute(20);      // 경로당 최대 연결 수

        return new RestTemplate(factory);
    }
}

