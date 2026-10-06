package com.winitech.smartAsset.domain.tangibleAsset;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AST-YYYY-NNNN 형식 자산코드 생성 - 자산 복제(S-214)처럼 여러 건을 연달아 발급해도
 * 서로 다른 코드가 나오는지 검증한다 (실제 DB의 asset_code_sequence를 사용하는 통합 테스트).
 */
@SpringBootTest
class AssetCodeGeneratorTest {

    @Autowired
    private AssetCodeGenerator assetCodeGenerator;

    @Test
    void 형식이_AST_YYYY_NNNN_이다() {
        String code = assetCodeGenerator.generate();

        assertThat(code).matches("AST-" + Year.now().getValue() + "-\\d{4}");
    }

    @Test
    void 여러_건을_연달아_생성해도_서로_다른_코드다() {
        int count = 20;
        Set<String> codes = new HashSet<>();

        for (int i = 0; i < count; i++) {
            codes.add(assetCodeGenerator.generate());
        }

        assertThat(codes).hasSize(count);
    }
}
