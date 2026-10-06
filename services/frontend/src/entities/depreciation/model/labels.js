/**
 * 감가상각 관련 라벨/옵션 (S-230~232)
 */

export const QUARTER_LABEL = {
  Q1: '1분기(1~3월)',
  Q2: '2분기(1~6월 누계)',
  Q3: '3분기(1~9월 누계)',
  Q4: '4분기(1~12월 누계)',
};

export const QUARTER_OPTIONS = Object.entries(QUARTER_LABEL).map(([value, label]) => ({
  value,
  label,
}));

export const EXCLUDED_REASON_LABEL = {
  NO_AMOUNT: '취득가액 없음',
  NO_BASIS: '상각 기준 없음',
  NOT_DEPRECIABLE_STATUS: '상각 대상 상태 아님',
};
