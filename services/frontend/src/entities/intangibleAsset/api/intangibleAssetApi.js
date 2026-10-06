/**
 * 무형자산(intangibleAsset) 엔티티 API
 */

const DATE_ONLY_PATTERN = /^\d{4}-\d{2}-\d{2}$/;

// 무형자산 API의 자동 생성 DTO는 날짜 필드를 OffsetDateTime으로 받고,
// 서버 매퍼가 정오 UTC를 LocalDate로 변환한다. 날짜만 입력된 경우에만 그 계약에 맞춘다.
const toIntangibleAssetApiDateTime = (value) => (
  typeof value === 'string' && DATE_ONLY_PATTERN.test(value) ? `${value}T12:00:00Z` : value
);

const withApiDateTimes = (body, dateFields) => {
  const converted = { ...body };
  dateFields.forEach((field) => {
    converted[field] = toIntangibleAssetApiDateTime(body?.[field]);
  });
  return converted;
};

/** S-500 목록 */
export const fetchIntangibleAssets = async (connector, { keyword, withinDays, page, size } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/intangible-asset', {
    params: { keyword, withinDays, page, size },
  });
  return response.data;
};

/** S-501 등록 */
export const createIntangibleAsset = async (connector, body) => {
  const response = await connector.client.post(
    '/api/v1/smart-asset/intangible-asset',
    withApiDateTimes(body, ['registeredDate', 'expiryDate']),
  );
  return response.data;
};

/** S-501 수정 */
export const updateIntangibleAsset = async (connector, intangibleAssetId, body) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/intangible-asset/${intangibleAssetId}`,
    withApiDateTimes(body, ['registeredDate', 'expiryDate']),
  );
  return response.data;
};

/** S-500 삭제(사용 해제) */
export const deleteIntangibleAsset = async (connector, intangibleAssetId) => {
  const response = await connector.client.delete(`/api/v1/smart-asset/intangible-asset/${intangibleAssetId}`);
  return response.data;
};

/** S-502 갱신 이력 */
export const fetchIntangibleAssetActionLog = async (connector, intangibleAssetId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/intangible-asset/${intangibleAssetId}/action-log`);
  return response.data;
};

/** S-502 갱신 */
export const renewIntangibleAsset = async (connector, intangibleAssetId, newExpiryDate, note) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/intangible-asset/${intangibleAssetId}/renew`,
    withApiDateTimes({ newExpiryDate, note }, ['newExpiryDate']),
  );
  return response.data;
};
