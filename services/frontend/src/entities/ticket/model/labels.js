export const TICKET_TYPE_LABEL = {
  PURCHASE: '신규구매',
  REPAIR: '수리',
  REPLACE: '교체',
  RETURN: '회수',
  EXTEND: '연장',
  DISPOSAL: '폐기',
};

export const TICKET_STATUS_LABEL = {
  WAITING: '접수대기',
  RECEIVED: '접수',
  IN_PROGRESS: '처리중',
  DONE: '완료',
};

/** 칸반 컬럼 순서 - DONE은 항상 마지막 */
export const TICKET_STATUS_ORDER = ['WAITING', 'RECEIVED', 'IN_PROGRESS', 'DONE'];
