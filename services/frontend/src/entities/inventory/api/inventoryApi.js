/**
 * 전수조사(inventory) 엔티티 API (S-300~306,308 관리자 화면)
 */

export const fetchInventoryList = async (connector, { page, size, sort } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/inventory', {
    params: { page: page ?? 0, size: size ?? 20, sort: sort || undefined },
  });
  return response.data;
};

export const fetchInventory = async (connector, inventoryId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/inventory/${inventoryId}`);
  return response.data;
};

/** S-301 대상 미리보기 - 실제로 생성하지 않는다 */
export const previewMemberInventory = async (connector, excludedMemberIds) => {
  const response = await connector.client.post('/api/v1/smart-asset/inventory/member/preview', {
    excludedMemberIds: excludedMemberIds || [],
  });
  return response.data;
};

export const registerMemberInventory = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/inventory/member', body);
  return response.data;
};

/** S-302 대상 미리보기 */
export const previewAdminInventory = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/inventory/admin/preview');
  return response.data;
};

export const registerAdminInventory = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/inventory/admin', body);
  return response.data;
};

/** S-303 진행 현황 - 4개 지표 + 참여자별 분해 */
export const fetchInventoryProgress = async (connector, inventoryId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/inventory/${inventoryId}/progress`);
  return response.data;
};

/** S-303(자산별/이상보고)·S-304·S-308이 함께 쓰는 목록 - status 생략 시 전체 */
export const fetchInventoryResults = async (connector, inventoryId, status) => {
  const response = await connector.client.get(`/api/v1/smart-asset/inventory/${inventoryId}/results`, {
    params: { status: status || undefined },
  });
  return response.data;
};

export const approveInventoryResult = async (connector, inventoryTargetId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/inventory-result/${inventoryTargetId}/approve`);
  return response.data;
};

export const rejectInventoryResult = async (connector, inventoryTargetId, reason) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/inventory-result/${inventoryTargetId}/reject`, { reason });
  return response.data;
};

/** 임직원 모바일 검수(S-310/311)가 아직 없어 관리자가 대신 확인 처리하는 임시 경로 */
export const adminConfirmInventoryResult = async (connector, inventoryTargetId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/inventory-result/${inventoryTargetId}/confirm`);
  return response.data;
};

export const adminReportInventoryAnomaly = async (connector, inventoryTargetId, anomalyType, note) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/inventory-result/${inventoryTargetId}/anomaly`, { anomalyType, note });
  return response.data;
};

/** S-308: 미확인 자산 종결 처리(1건) */
export const closeInventoryResult = async (connector, inventoryTargetId, closureAction, closureReasonCode, note) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/inventory-result/${inventoryTargetId}/close`, {
    closureAction, closureReasonCode, note,
  });
  return response.data;
};

/** S-308: 미확인 자산 종결 처리(일괄) - 분실(LOST)은 서버에서 거부된다(R6) */
export const closeInventoryResultsBulk = async (connector, inventoryTargetIds, closureAction, closureReasonCode, note) => {
  const response = await connector.client.patch('/api/v1/smart-asset/inventory-result/close-batch', {
    inventoryTargetIds, closureAction, closureReasonCode, note,
  });
  return response.data;
};

/** S-308: 조사 종료 확정 - 미확인 중 종결 처리가 안 된 항목이 남아있으면 서버가 막는다 */
export const closeInventory = async (connector, inventoryId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/inventory/${inventoryId}/close`);
  return response.data;
};

/** S-305 리포트 - 4개 지표·참여자별 분해·미확인 처리 내역·분실 목록·이상 유형 분해 (진행중/종료 모두 조회 가능) */
export const fetchInventoryReport = async (connector, inventoryId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/inventory/${inventoryId}/report`);
  return response.data;
};

/** S-305 리포트 엑셀 다운로드(참고용) - PDF는 이 서비스에 PDF 생성 인프라가 없어 미구현 */
export const fetchInventoryReportExcel = async (connector, inventoryId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/inventory/${inventoryId}/report/excel`, {
    responseType: 'blob',
  });
  // 공통 응답 인터셉터가 JSON이 아닌 응답을 {result:null, data:<원본>}으로 감싸므로 한 겹 더 풀어준다
  return response.data?.data ?? response.data;
};

/** S-306 반복 시행 스케줄 - 유형별 최근 종료된 조사 기준 다음 시행 예정일(자동 실행은 미구현) */
export const fetchInventorySchedules = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/inventory/schedules');
  return response.data;
};

/** S-306 템플릿 복제 - 선택한 조사의 생성 옵션을 그대로 읽어 새 생성 다이얼로그를 미리 채운다 */
export const fetchInventoryCloneTemplate = async (connector, inventoryId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/inventory/${inventoryId}/clone-template`);
  return response.data;
};

/** S-310 내 전수조사 - 본인이 배정받은, 진행 중인 조사의 대상 목록(I2: 최대 1건) */
export const fetchMyInventoryStatus = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/inventory/my');
  return response.data;
};

/** S-311 본인 QR 스캔 확인 - 본인 소유 대상만 허용(소유권 검사, 메뉴 권한과 무관) */
export const selfConfirmInventoryResult = async (connector, inventoryTargetId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/inventory-result/${inventoryTargetId}/self-confirm`);
  return response.data;
};

/** S-311 I3 예외 경로 - 라벨 없음/훼손: 스캔 없이 사진으로 확인, 항상 승인대기가 된다 */
export const selfConfirmInventoryResultWithoutScan = async (connector, inventoryTargetId, photoFileId, capturedAt, uploadedAt) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/inventory-result/${inventoryTargetId}/self-confirm-photo`, {
    photoFileId, capturedAt, uploadedAt,
  });
  return response.data;
};

/** S-311 "제 자산이 아닙니다" - 본인 소유 대상에 대해 타인보유 이상 보고 */
export const selfReportWrongHolder = async (connector, inventoryTargetId, note) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/inventory-result/${inventoryTargetId}/self-wrong-holder`, { note });
  return response.data;
};
