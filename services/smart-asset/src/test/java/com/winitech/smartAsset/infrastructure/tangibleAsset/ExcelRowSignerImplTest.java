package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelRow;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ExcelRowSignerImpl의 서명/검증 로직과, 서명 키 자체의 유효성(32바이트 이상) 검증을 다룬다.
 * 이 클래스는 Spring 컨텍스트 없이 생성자를 직접 호출하는 순수 단위 테스트다 - 생성자가 던지는
 * 예외가 곧 "이 빈을 만들 때 애플리케이션 기동이 실패한다"는 것과 동일하므로(Spring이 그 예외를
 * BeanCreationException으로 감싸 컨텍스트 시작을 중단시킨다), 전체 컨텍스트를 띄우지 않고도
 * 빠르게 그 실패 조건을 검증할 수 있다. 컨텍스트 수준에서의 같은 시나리오는
 * ExcelRowSignerConfigurationTest에서 별도로 검증한다.
 */
class ExcelRowSignerImplTest {

    private static final String VALID_KEY = "unit-test-signing-key-with-32-plus-bytes-of-length";
    private static final String OTHER_VALID_KEY = "a-completely-different-32-byte-plus-signing-key!!";

    private TangibleAssetExcelRow.TangibleAssetExcelRowBuilder rowBuilder() {
        return TangibleAssetExcelRow.builder()
                .rowNum(1)
                .action("CREATE")
                .assetName("노트북")
                .categoryId(UUID.randomUUID())
                .locationId(UUID.randomUUID())
                .lifeStatus("USE")
                .assignType("UNASSIGNED")
                .acquisitionDate("2026-01-01")
                .acquisitionAmount("1000000")
                .modelName("")
                .manufacturer("")
                .serialNo("")
                .memo("");
    }

    @Test
    void 유효한_32바이트_이상_키로_구성에_성공한다() {
        ExcelRowSignerImpl signer = new ExcelRowSignerImpl(VALID_KEY);

        TangibleAssetExcelRow row = rowBuilder().build();
        assertThat(signer.sign(row)).isNotBlank();
    }

    @Test
    void 키가_null이면_구성에_실패한다() {
        assertThatThrownBy(() -> new ExcelRowSignerImpl(null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 키가_빈_문자열이면_구성에_실패한다() {
        assertThatThrownBy(() -> new ExcelRowSignerImpl(""))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 키가_32바이트_미만이면_구성에_실패한다() {
        assertThatThrownBy(() -> new ExcelRowSignerImpl("short-key-31-bytes-long-exactly"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 정상_서명은_같은_행에_대해_검증에_성공한다() {
        ExcelRowSignerImpl signer = new ExcelRowSignerImpl(VALID_KEY);

        TangibleAssetExcelRow row = rowBuilder().build();
        row.setSignature(signer.sign(row));

        assertThat(signer.verify(row)).isTrue();
    }

    @Test
    void 행_데이터가_변조되면_검증에_실패한다() {
        ExcelRowSignerImpl signer = new ExcelRowSignerImpl(VALID_KEY);

        TangibleAssetExcelRow row = rowBuilder().build();
        row.setSignature(signer.sign(row));
        row.setCategoryId(UUID.randomUUID()); // 서명 계산 이후 변조

        assertThat(signer.verify(row)).isFalse();
    }

    @Test
    void 다른_키로_생성한_서명은_검증에_실패한다() {
        ExcelRowSignerImpl signedWith = new ExcelRowSignerImpl(VALID_KEY);
        ExcelRowSignerImpl verifiedWith = new ExcelRowSignerImpl(OTHER_VALID_KEY);

        TangibleAssetExcelRow row = rowBuilder().build();
        row.setSignature(signedWith.sign(row));

        assertThat(verifiedWith.verify(row)).isFalse();
    }

    @Test
    void null_서명은_안전하게_false를_반환한다() {
        ExcelRowSignerImpl signer = new ExcelRowSignerImpl(VALID_KEY);

        TangibleAssetExcelRow row = rowBuilder().build();
        row.setSignature(null);

        assertThat(signer.verify(row)).isFalse();
    }

    @Test
    void base64_형식이_아닌_서명도_안전하게_false를_반환한다() {
        ExcelRowSignerImpl signer = new ExcelRowSignerImpl(VALID_KEY);

        TangibleAssetExcelRow row = rowBuilder().build();
        row.setSignature("이건-base64가-아님!!");

        assertThat(signer.verify(row)).isFalse();
    }
}
