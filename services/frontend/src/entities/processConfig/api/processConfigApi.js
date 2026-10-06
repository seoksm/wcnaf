/**
 * 프로세스 설정(processConfig) 엔티티 API
 */

/**
 * 프로세스 설정 조회 (S-400, 워크스페이스당 단일 행)
 */
export const fetchProcessConfig = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/process-config');
  return response.data;
};

/**
 * 프로세스 설정 수정
 */
export const updateProcessConfig = async (connector, body) => {
  const response = await connector.client.patch('/api/v1/smart-asset/process-config', body);
  return response.data;
};
