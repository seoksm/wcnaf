/**
 * 자산 2축 상태 모델 (Q-15) 표시 라벨
 */

export const LIFE_STATUS_LABEL = {
  USE: '사용',
  STORAGE: '보관',
  REPAIR: '수리중',
  DISUSE: '불용',
  DISPOSED: '처분완료',
};

export const ASSIGN_TYPE_LABEL = {
  UNASSIGNED: '미배정',
  PERSONAL: '개인배정',
  SHARED: '공용',
  LOANABLE: '대여가능',
  ON_LOAN: '대여중',
};

export const LIFE_STATUS_OPTIONS = Object.entries(LIFE_STATUS_LABEL).map(([value, label]) => ({ value, label }));
export const ASSIGN_TYPE_OPTIONS = Object.entries(ASSIGN_TYPE_LABEL).map(([value, label]) => ({ value, label }));

/** 배정 형태 중 특정 사용자(memberId)를 필요로 하는 것 (백엔드 TangibleAsset.requiresMember와 동일 규칙) */
export const requiresMember = (assignType) => assignType === 'PERSONAL' || assignType === 'ON_LOAN';

/** 처분 사유 (S-242) - 백엔드 DisposalAsset.DisposalReason과 동일 */
export const DISPOSAL_REASON_CODE_LABEL = {
  SALE: '매각',
  SCRAP: '폐기',
  DONATION: '기부',
  LOSS: '분실',
  THEFT: '도난',
  OTHER: '기타',
};

export const DISPOSAL_REASON_CODE_OPTIONS = Object.entries(DISPOSAL_REASON_CODE_LABEL).map(([value, label]) => ({ value, label }));

/** 자산 히스토리 유형 (S-220/221) - INVENTORY/LOAN/ACKNOWLEDGEMENT는 해당 기능 구현 전이라 아직 생성되지 않음 */
export const HISTORY_TYPE_LABEL = {
  REGISTER: '등록',
  MODIFY: '정보수정',
  STATUS_CHANGE: '상태변경',
  ASSIGNMENT: '배정',
  INVENTORY: '전수조사',
  LOAN: '대여',
  ACKNOWLEDGEMENT: '확인서',
  DISUSE: '불용',
  DISPOSAL: '처분',
};

export const HISTORY_TYPE_OPTIONS = Object.entries(HISTORY_TYPE_LABEL).map(([value, label]) => ({ value, label }));
