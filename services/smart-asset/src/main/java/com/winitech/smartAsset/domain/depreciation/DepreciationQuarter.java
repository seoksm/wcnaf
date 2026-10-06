package com.winitech.smartAsset.domain.depreciation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** D2: 산출 단위는 회계연도 누적 분기 4종뿐 - 월별 자료는 만들지 않는다 */
@Getter
@RequiredArgsConstructor
public enum DepreciationQuarter {
    Q1("1~3월", 3),
    Q2("1~6월", 6),
    Q3("1~9월", 9),
    Q4("1~12월", 12);

    private final String description;
    private final int endMonth;
}
