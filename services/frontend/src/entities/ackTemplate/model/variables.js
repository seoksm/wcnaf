/** S-433/S-430 공용 - 확인서 문구에 쓸 수 있는 치환 변수 (설계문서 §4) */
export const ACK_TEMPLATE_VARIABLES = ['자산명', '자산코드', '취득가액', '지급일', '담당자', '대상자'];

export const ACK_TYPE_LABEL = { RECEIPT: '수령', RETURN: '반납' };

/** 저장 전 검증 - {{미정의변수}} 오타를 저장 시점에 막는다(설계문서 §4) */
export const findUndefinedVariables = (bodyTpl) => {
  const matches = [...(bodyTpl || '').matchAll(/\{\{(.+?)\}\}/g)].map((m) => m[1]);
  return [...new Set(matches)].filter((name) => !ACK_TEMPLATE_VARIABLES.includes(name));
};
