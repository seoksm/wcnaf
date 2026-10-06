/**
 * 확인서 문구(ackTemplate) 엔티티 API
 */

/** S-433 확인서 문구 2종 조회 */
export const fetchAckTemplates = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/ack-template');
  return response.data;
};

/** S-433 확인서 문구 수정 */
export const updateAckTemplate = async (connector, type, bodyTpl) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/ack-template/${type}`, { bodyTpl });
  return response.data;
};
