/**
 * S-700/701 위젯 10종 레지스트리 - 백엔드 DashboardWidgetKey와 동일한 순서(설계문서 §2
 * "요약→분포→추세→조치필요→프로세스현황" 배치 순서 = enum 선언 순서 = 기본 sortOrder).
 */
export const WIDGET_KEYS = [
  'ASSET_SUMMARY',
  'CATEGORY_DISTRIBUTION',
  'LOCATION_DISTRIBUTION',
  'STATUS_DISTRIBUTION',
  'MONTHLY_COST_TREND',
  'EXPIRING_SOON',
  'TICKET_STATUS',
  'LOAN_STATUS',
  'INVENTORY_PROGRESS',
  'LICENSE_CONSISTENCY',
];

export const WIDGET_LABEL = {
  ASSET_SUMMARY: '자산 총계',
  CATEGORY_DISTRIBUTION: '종류별 현황',
  LOCATION_DISTRIBUTION: '위치별 현황',
  STATUS_DISTRIBUTION: '상태별 현황',
  MONTHLY_COST_TREND: '월별 비용 추이',
  EXPIRING_SOON: '만료 예정',
  TICKET_STATUS: '티켓 현황',
  LOAN_STATUS: '대여 현황',
  INVENTORY_PROGRESS: '전수조사 진행률',
  LICENSE_CONSISTENCY: '라이선스 정합성',
};

/** 대여 프로세스가 꺼져 있으면 숨기는 위젯 (Step 6 §1 매트릭스와 동일한 프로세스 의존) */
export const PROCESS_DEPENDENT_WIDGETS = {
  LOAN_STATUS: 'loanEnabled',
};

/** 저장된 설정이 없을 때(신규 관리자) 쓰는 기본값 - 전체 표시, 선언 순서 */
export const defaultWidgetConfig = () => WIDGET_KEYS.map((widgetKey, index) => ({
  widgetKey,
  visible: true,
  sortOrder: index,
}));
