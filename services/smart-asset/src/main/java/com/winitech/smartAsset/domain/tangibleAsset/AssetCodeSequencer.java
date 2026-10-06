package com.winitech.smartAsset.domain.tangibleAsset;

/**
 * 자산코드(AST-YYYY-NNNN) 채번을 위한 연도별 순번 발급 포트.
 * 동시에 여러 요청이 들어와도 같은 연도에 같은 순번이 두 번 발급되지 않아야 한다.
 */
public interface AssetCodeSequencer {

    /**
     * 지정한 연도의 다음 순번을 원자적으로 발급한다. 연도가 처음 등장하면 1부터 시작한다.
     */
    int nextSequence(int fiscalYear);
}
