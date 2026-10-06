/**
 * 유형자산(tangibleAsset) 엔티티 API
 */

/**
 * 유형자산 목록 조회 (페이지네이션) - page는 0부터, size 기본 20/최대 200, sort는 "필드,방향"
 */
export const fetchTangibleAssets = async (connector, { keyword, page, size, sort } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/tangible-asset', {
    params: {
      keyword: keyword || undefined,
      page: page ?? 0,
      size: size ?? 20,
      sort: sort || undefined,
    },
  });
  return response.data;
};

/**
 * 유형자산 단건 상세 조회
 */
export const fetchTangibleAsset = async (connector, tangibleAssetId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/tangible-asset/${tangibleAssetId}`);
  return response.data;
};

/**
 * 유형자산 등록
 */
export const createTangibleAsset = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/tangible-asset', body);
  return response.data;
};

/**
 * 유형자산 수정
 */
export const updateTangibleAsset = async (connector, tangibleAssetId, body) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/tangible-asset/${tangibleAssetId}`,
    body,
  );
  return response.data;
};

/**
 * 배정 대상 후보 - 공용 사용자 목록 조회
 */
export const fetchCommonUsers = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/commonUser');
  return response.data;
};

/**
 * 유형자산 배정 이력 조회 (S-218)
 */
export const fetchAssignmentHistory = async (connector, tangibleAssetId) => {
  const response = await connector.client.get(
    `/api/v1/smart-asset/tangible-asset/${tangibleAssetId}/assignment-history`,
  );
  return response.data;
};

/**
 * 유형자산 배정 회수 (S-218)
 */
export const releaseAssignment = async (connector, tangibleAssetId) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/tangible-asset/${tangibleAssetId}/release`,
  );
  return response.data;
};

/**
 * 유형자산 복제 (S-214)
 */
export const duplicateTangibleAsset = async (connector, tangibleAssetId, count) => {
  const response = await connector.client.post(
    `/api/v1/smart-asset/tangible-asset/${tangibleAssetId}/duplicate`,
    { count },
  );
  return response.data;
};

/**
 * 유형자산 일괄 변경 (S-215)
 */
export const batchModifyTangibleAsset = async (connector, body) => {
  const response = await connector.client.patch('/api/v1/smart-asset/tangible-asset/batch', body);
  return response.data;
};

/**
 * 유형자산 엑셀 등록 양식 다운로드 (S-213)
 */
export const fetchTangibleAssetExcelTemplate = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/tangible-asset/excel/template', {
    responseType: 'blob',
  });
  // 공통 응답 인터셉터가 JSON이 아닌 응답을 {result:null, data:<원본>}으로 감싸므로 한 겹 더 풀어준다
  return response.data?.data ?? response.data;
};

/**
 * 유형자산 엑셀 업서트 미리보기 - 저장하지 않고 검증 결과만 반환 (S-213)
 */
export const previewTangibleAssetExcelUpsert = async (connector, file) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await connector.client.post('/api/v1/smart-asset/tangible-asset/excel/preview', formData, {
    headers: { 'Content-Type': undefined },
  });
  return response.data;
};

/**
 * 유형자산 엑셀 업서트 확정 (S-213) - commitId는 "확정" 한 번의 시도마다 고유해야 하며,
 * 서버가 이 값으로 중복 반영을 막는다(같은 commitId로 재시도하면 이전 결과를 그대로 돌려받는다).
 */
export const commitTangibleAssetExcelUpsert = async (connector, commitId, rows) => {
  const response = await connector.client.post('/api/v1/smart-asset/tangible-asset/excel/commit', {
    commitId,
    rows,
  });
  return response.data;
};

/**
 * 유형자산 1건의 변경 이력 조회 (S-220)
 */
export const fetchTangibleAssetHistory = async (connector, tangibleAssetId) => {
  const response = await connector.client.get(
    `/api/v1/smart-asset/tangible-asset/${tangibleAssetId}/history`,
  );
  return response.data;
};

/**
 * 전체 활동 로그 조회 - filter의 각 값이 없으면 해당 조건 미적용, page는 0부터(페이지당 200건) (S-221)
 */
export const fetchTangibleAssetActivityLog = async (connector, filter, page) => {
  const response = await connector.client.get('/api/v1/smart-asset/tangible-asset/activity-log', {
    params: {
      historyType: filter?.historyType || undefined,
      assetCode: filter?.assetCode || undefined,
      assetName: filter?.assetName || undefined,
      fromDate: filter?.fromDate || undefined,
      toDate: filter?.toDate || undefined,
      page: page || 0,
    },
  });
  return response.data;
};

/**
 * 불용 처리 - 사용/보관/수리중 상태에서만 가능, 배정이 있으면 자동 해제된다 (S-241, A1)
 */
export const disuseTangibleAsset = async (connector, tangibleAssetId, reason) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/tangible-asset/${tangibleAssetId}/disuse`,
    { reason: reason || undefined },
  );
  return response.data;
};

/**
 * 불용 → 사용 복귀 (S-240, Q-28 - 처분완료는 복귀 불가)
 */
export const restoreTangibleAsset = async (connector, tangibleAssetId) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/tangible-asset/${tangibleAssetId}/restore`,
  );
  return response.data;
};

/**
 * 처분 처리 - 불용 상태에서만 가능하며 되돌릴 수 없다 (S-242, Q-26, Q-28)
 */
export const disposeTangibleAsset = async (connector, tangibleAssetId, body) => {
  const response = await connector.client.post(
    `/api/v1/smart-asset/tangible-asset/${tangibleAssetId}/dispose`,
    body,
  );
  return response.data;
};

/**
 * 불용자산 목록 조회 (페이지네이션) - lifeStatus 생략 시 불용+처분완료 전체 (S-240)
 */
export const fetchDisposalAssetList = async (connector, { lifeStatus, page, size, sort } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/tangible-asset/disposal', {
    params: {
      lifeStatus: lifeStatus || undefined,
      page: page ?? 0,
      size: size ?? 20,
      sort: sort || undefined,
    },
  });
  return response.data;
};
