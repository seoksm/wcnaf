package com.winitech.smartAsset.infrastructure.tangibleAsset;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ExcelRowSignerImpl을 실제 Spring 빈으로 구성할 때, winitech.smart-asset.excel-upsert.signing-key
 * 프로퍼티(운영에서는 환경변수 SMART_ASSET_EXCEL_SIGNING_KEY로 주입됨) 값에 따라 애플리케이션
 * 컨텍스트 자체가 기동에 성공/실패해야 한다는 요구사항을 검증한다. 전체 @SpringBootTest 대신
 * ApplicationContextRunner로 이 빈 하나만 올려 빠르게 확인한다 - PropertySourcesPlaceholderConfigurer를
 * 함께 등록해야 @Value("${...}")의 플레이스홀더가 실제로 해석된다.
 */
class ExcelRowSignerConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(PropertySourcesPlaceholderConfigurer.class)
            .withBean(ExcelRowSignerImpl.class);

    @Test
    void 유효한_32바이트_이상_키로_애플리케이션_구성에_성공한다() {
        contextRunner
                .withPropertyValues("winitech.smart-asset.excel-upsert.signing-key=this-is-a-valid-test-key-with-plenty-of-bytes")
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void 키가_누락되면_기동에_실패한다() {
        // 프로퍼티 자체를 아예 지정하지 않아, @Value의 빈 문자열 기본값으로 해석되는 경우
        contextRunner.run(context -> assertThat(context).hasFailed());
    }

    @Test
    void 키가_빈_문자열이면_기동에_실패한다() {
        // 배포 설정 파일이 ${SMART_ASSET_EXCEL_SIGNING_KEY:}로 두고 환경변수가 없을 때 실제로
        // 벌어지는 상황과 동일하다.
        contextRunner
                .withPropertyValues("winitech.smart-asset.excel-upsert.signing-key=")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void 키가_너무_짧으면_기동에_실패한다() {
        contextRunner
                .withPropertyValues("winitech.smart-asset.excel-upsert.signing-key=short-key")
                .run(context -> assertThat(context).hasFailed());
    }
}
