/**
 * 감가상각(depreciation) 엔티티 API (S-230~232)
 */

/**
 * 감가상각 현황 조회 (S-230)
 */
export const fetchDepreciationStatus = async (connector, fiscalYear, quarter) => {
  const response = await connector.client.get('/api/v1/smart-asset/depreciation', {
    params: { fiscalYear, quarter },
  });
  return response.data;
};

/**
 * 자산별 상각 스케줄 조회 (S-231)
 */
export const fetchDepreciationSchedule = async (connector, tangibleAssetId, fiscalYear) => {
  const response = await connector.client.get(
    `/api/v1/smart-asset/tangible-asset/${tangibleAssetId}/depreciation-schedule`,
    { params: { fiscalYear } },
  );
  return response.data;
};

/**
 * 결산 확정 (S-232)
 */
export const confirmDepreciation = async (connector, body) => {
  const response = await connector.client.patch('/api/v1/smart-asset/depreciation/confirm', body);
  return response.data;
};

/**
 * 결산 확정 해제 (S-232)
 */
export const releaseDepreciation = async (connector, body) => {
  const response = await connector.client.patch('/api/v1/smart-asset/depreciation/release', body);
  return response.data;
};

/**
 * 확정·해제 이력 조회 (S-232)
 */
export const fetchDepreciationConfirmationLog = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/depreciation/confirmation-log');
  return response.data;
};
