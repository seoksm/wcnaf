/**
 * 라이선스(license) 엔티티 API
 */

const DATE_ONLY_PATTERN = /^\d{4}-\d{2}-\d{2}$/;

// 라이선스 API의 자동 생성 DTO는 날짜 필드를 OffsetDateTime으로 받고,
// 서버 매퍼가 정오 UTC를 LocalDate로 변환한다. 날짜만 입력된 경우에만 그 계약에 맞춘다.
const toLicenseApiDateTime = (value) => (
  typeof value === 'string' && DATE_ONLY_PATTERN.test(value) ? `${value}T12:00:00Z` : value
);

const withApiDateTimes = (body, dateFields) => {
  const converted = { ...body };
  dateFields.forEach((field) => {
    converted[field] = toLicenseApiDateTime(body?.[field]);
  });
  return converted;
};

export const fetchLicenses = async (connector, { keyword, page, size } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/license', { params: { keyword, page, size } });
  return response.data;
};

export const fetchMyLicenses = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/license/my');
  return response.data;
};

export const fetchLicense = async (connector, licenseId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/license/${licenseId}`);
  return response.data;
};

export const createLicense = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/license', body);
  return response.data;
};

export const updateLicense = async (connector, licenseId, body) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/license/${licenseId}`, body);
  return response.data;
};

export const deleteLicense = async (connector, licenseId) => {
  const response = await connector.client.delete(`/api/v1/smart-asset/license/${licenseId}`);
  return response.data;
};

export const fetchLicensePurchaseRecords = async (connector, licenseId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/license/${licenseId}/purchase-record`);
  return response.data;
};

export const addLicensePurchaseRecord = async (connector, licenseId, body) => {
  const response = await connector.client.post(
    `/api/v1/smart-asset/license/${licenseId}/purchase-record`,
    withApiDateTimes(body, ['purchaseDate']),
  );
  return response.data;
};

export const updateLicensePurchaseRecord = async (connector, licensePurchaseRecordId, body) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/license-purchase-record/${licensePurchaseRecordId}`,
    withApiDateTimes(body, ['purchaseDate']),
  );
  return response.data;
};

export const fetchLicenseAssignedUsers = async (connector, licenseId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/license/${licenseId}/assigned-user`);
  return response.data;
};

export const assignLicenseUser = async (connector, licenseId, body) => {
  const response = await connector.client.post(`/api/v1/smart-asset/license/${licenseId}/assigned-user`, body);
  return response.data;
};

export const requestReleaseLicenseAssignedUser = async (connector, licenseAssignedUserId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/license-assigned-user/${licenseAssignedUserId}/request-release`);
  return response.data;
};

export const releaseLicenseAssignedUser = async (connector, licenseAssignedUserId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/license-assigned-user/${licenseAssignedUserId}/release`);
  return response.data;
};

export const fetchLicenseIncludedSoftware = async (connector, licenseId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/license/${licenseId}/included-software`);
  return response.data;
};

export const addLicenseIncludedSoftware = async (connector, licenseId, softwareName) => {
  const response = await connector.client.post(`/api/v1/smart-asset/license/${licenseId}/included-software`, { softwareName });
  return response.data;
};

export const removeLicenseIncludedSoftware = async (connector, licenseIncludedSoftwareId) => {
  const response = await connector.client.delete(`/api/v1/smart-asset/license-included-software/${licenseIncludedSoftwareId}`);
  return response.data;
};
