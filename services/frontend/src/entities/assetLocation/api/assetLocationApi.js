/**
 * 자산 위치(assetLocation) 엔티티 API
 */

/**
 * 자산 위치 목록 조회
 */
export const fetchAssetLocations = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/asset-location');
  return response.data;
};

/**
 * 자산 위치 등록
 */
export const createAssetLocation = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/asset-location', body);
  return response.data;
};

/**
 * 자산 위치 수정
 */
export const updateAssetLocation = async (connector, locationId, body) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/asset-location/${locationId}`,
    body,
  );
  return response.data;
};

/**
 * 자산 위치 삭제
 */
export const deleteAssetLocation = async (connector, locationId) => {
  const response = await connector.client.delete(
    `/api/v1/smart-asset/asset-location/${locationId}`,
  );
  return response.data;
};
