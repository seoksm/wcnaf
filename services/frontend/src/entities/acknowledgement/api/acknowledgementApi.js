/**
 * 확인서(acknowledgement) 엔티티 API
 */

/** S-431 확인서 현황 목록 */
export const fetchAcknowledgementList = async (connector, { status, page, size } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/acknowledgement', {
    params: { status, page, size },
  });
  return response.data;
};

/** S-432 확인서 상세 (관리자) */
export const fetchAcknowledgementDetail = async (connector, acknowledgementId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/acknowledgement/${acknowledgementId}`);
  return response.data;
};

/** S-430 확인서 요청 */
export const createAcknowledgement = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/acknowledgement', body);
  return response.data;
};

/** S-432 담당자 승인 */
export const managerApproveAcknowledgement = async (connector, acknowledgementId, body) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/acknowledgement/${acknowledgementId}/manager-approve`, body);
  return response.data;
};

/** S-431 요청 취소 */
export const cancelAcknowledgement = async (connector, acknowledgementId, reason) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/acknowledgement/${acknowledgementId}/cancel`, { reason });
  return response.data;
};

/** S-440 내 확인서 승인대기 목록 */
export const fetchMyAcknowledgements = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/acknowledgement/my');
  return response.data;
};

/** S-440 내 확인서 상세 */
export const fetchMyAcknowledgementDetail = async (connector, acknowledgementId) => {
  const response = await connector.client.get(`/api/v1/smart-asset/acknowledgement/my/${acknowledgementId}`);
  return response.data;
};

/** S-440 임직원 본인 승인 */
export const approveAcknowledgementBySelf = async (connector, acknowledgementId) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/acknowledgement/${acknowledgementId}/employee-approve`);
  return response.data;
};
