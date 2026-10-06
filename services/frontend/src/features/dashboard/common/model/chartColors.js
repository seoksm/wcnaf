/**
 * S-700 대시보드 색 규약 (설계문서 §1 V2/V5)
 * - V2: 범주색은 고정 순서로 배정하고 순환시키지 않는다 - 인덱스 기반으로 항상 같은 색을 쓴다.
 * - V5: 상태색(정상·경고·위험)은 예약색이다 - 범주색 배열에 다시 넣지 않는다.
 */

/** 종류별/위치별 현황 - 7범주 상한(상위6+기타)에 맞춰 7개 고정 색 (마지막은 "기타") */
export const CATEGORY_COLORS = ['#2563eb', '#16a34a', '#f59e0b', '#8b5cf6', '#06b6d4', '#ec4899', '#9ca3af'];

/**
 * 상태별 현황(life_status) - 5종 고정 배정, 순환 없음. 백엔드가 이미 한글 설명(예: "사용")으로
 * 변환해 내려주므로(TangibleAsset.LifeStatus.getDescription()) 그 한글 라벨을 키로 쓴다 -
 * 원시 GROUP BY 결과의 행 순서는 보장되지 않아 "순서가 아니라 대상을 따르는 색"(V2)에는
 * 이름 기반 매핑이 순서 기반보다 안전하다.
 */
export const LIFE_STATUS_COLORS = {
  사용: '#2563eb',
  보관: '#9ca3af',
  수리중: '#f59e0b',
  불용: '#ea580c',
  처분완료: '#6b7280',
};

/** 월별 비용추이 - 렌탈/라이선스 2계열 고정 배정 */
export const COST_TREND_COLORS = {
  rental: '#2563eb',
  license: '#8b5cf6',
};

/** 예약 상태색(정상·경고·위험) - 범주색으로 재사용 금지 */
export const SEMANTIC_COLORS = {
  normal: '#16a34a',
  warning: '#ea580c',
  critical: '#dc2626',
};
