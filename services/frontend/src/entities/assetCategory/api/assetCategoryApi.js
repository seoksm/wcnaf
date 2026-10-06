/**
 * 자산 종류(assetCategory) 엔티티 API
 */

/**
 * 자산 종류 목록 조회
 */
export const fetchAssetCategories = async (connector) => {
  const response = await connector.client.get('/api/v1/smart-asset/asset-category');
  return response.data;
};

/**
 * 자산 종류 등록
 */
export const createAssetCategory = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/asset-category', body);
  return response.data;
};

/**
 * 자산 종류 수정
 */
export const updateAssetCategory = async (connector, categoryId, body) => {
  const response = await connector.client.patch(
    `/api/v1/smart-asset/asset-category/${categoryId}`,
    body,
  );
  return response.data;
};

/**
 * 자산 종류 삭제
 */
export const deleteAssetCategory = async (connector, categoryId) => {
  const response = await connector.client.delete(
    `/api/v1/smart-asset/asset-category/${categoryId}`,
  );
  return response.data;
};
