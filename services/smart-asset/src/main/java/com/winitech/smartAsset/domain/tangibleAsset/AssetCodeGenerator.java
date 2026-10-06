package com.winitech.smartAsset.domain.tangibleAsset;

import com.winitech.common.exception.IllegalStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;

/**
 * Q-14: AST-YYYY-NNNN 형식 자산코드 자동 채번 (연도별 순번 리셋).
 * 실제 순번 발급은 {@link AssetCodeSequencer}(원자적 DB 카운터)에 위임하므로 동시 요청에도 안전하다.
 */
@Component
@RequiredArgsConstructor
public class AssetCodeGenerator {

    /** 순번 자리수(NNNN)를 4자리로 고정하는 현재 형식을 유지하기 위한 상한 - 이 값을 넘기면 형식을
     * 5자리 이상으로 늘리는 대신, 해당 연도의 신규 발급 자체를 막는다(설계 선택, 아래 참고). */
    private static final int MAX_SEQUENCE_PER_YEAR = 9999;

    private final AssetCodeSequencer assetCodeSequencer;

    public String generate() {
        int fiscalYear = Year.now().getValue();
        int seq = assetCodeSequencer.nextSequence(fiscalYear);
        if (seq > MAX_SEQUENCE_PER_YEAR) {
            throw new IllegalStatusException(
                    fiscalYear + "년에 발급 가능한 자산코드 순번(최대 " + MAX_SEQUENCE_PER_YEAR + "건)을 모두 사용했습니다. "
                            + "관리자에게 문의해주세요.");
        }
        return String.format("AST-%d-%04d", fiscalYear, seq);
    }
}
