package com.winitech.smartAsset.domain.dashboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * S-700 위젯 10종의 키 - S-701 개인화 설정(dashboard_widget_config)의 대상이자, 화면 배치
 * 기본 순서(설계문서 §2 "요약→분포→추세→조치필요→프로세스현황")의 정의부다. 설정 행이 없는
 * 위젯은 이 enum 선언 순서를 기본 sortOrder로 쓴다.
 */
@Getter
@RequiredArgsConstructor
public enum DashboardWidgetKey {
    ASSET_SUMMARY("자산 총계"),
    CATEGORY_DISTRIBUTION("종류별 현황"),
    LOCATION_DISTRIBUTION("위치별 현황"),
    STATUS_DISTRIBUTION("상태별 현황"),
    MONTHLY_COST_TREND("월별 비용 추이"),
    EXPIRING_SOON("만료 예정"),
    TICKET_STATUS("티켓 현황"),
    LOAN_STATUS("대여 현황"),
    INVENTORY_PROGRESS("전수조사 진행률"),
    LICENSE_CONSISTENCY("라이선스 정합성");

    private final String description;
}
