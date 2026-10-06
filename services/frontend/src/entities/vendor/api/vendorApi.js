/**
 * 공급사(vendor) 엔티티 API
 */

export const fetchVendors = async (connector, { keyword } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/vendor', { params: { keyword } });
  return response.data;
};

export const createVendor = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/vendor', body);
  return response.data;
};

export const updateVendor = async (connector, vendorId, body) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/vendor/${vendorId}`, body);
  return response.data;
};

export const deleteVendor = async (connector, vendorId) => {
  const response = await connector.client.delete(`/api/v1/smart-asset/vendor/${vendorId}`);
  return response.data;
};
