/**
 * 전수조사 상태 모델 표시 라벨
 */

export const INVENTORY_TYPE_LABEL = {
  MEMBER: '임직원형',
  ADMIN: '관리자형',
};

export const INVENTORY_STATUS_LABEL = {
  IN_PROGRESS: '진행중',
  CLOSED: '종료',
};

export const RECURRENCE_RULE_LABEL = {
  QUARTERLY: '분기',
  SEMIANNUAL: '반기',
  ANNUAL: '연1회',
};

export const INVENTORY_RESULT_STATUS_LABEL = {
  UNCONFIRMED: '미확인',
  PENDING_APPROVAL: '승인대기',
  CONFIRMED: '확인완료',
  ANOMALY: '이상',
};

export const ANOMALY_TYPE_LABEL = {
  DAMAGE: '파손',
  LOCATION_MISMATCH: '위치불일치',
  WRONG_HOLDER: '타인보유',
  OTHER: '기타',
};

export const CLOSURE_ACTION_LABEL = {
  CARRY_OVER: '차기이월',
  MANUAL_VERIFY: '소재확인',
  LOST: '분실',
};

export const CLOSURE_REASON_CODE_LABEL = {
  NOT_PARTICIPATED: '미참여',
  ON_LEAVE: '휴직',
  LABEL_DAMAGED: '라벨훼손',
  LOCATION_UNKNOWN: '소재불명',
};
