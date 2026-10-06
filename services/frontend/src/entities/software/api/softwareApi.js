/**
 * 소프트웨어 마스터(software) 엔티티 API
 */

export const fetchSoftwareList = async (connector, { keyword } = {}) => {
  const response = await connector.client.get('/api/v1/smart-asset/software', { params: { keyword } });
  return response.data;
};

export const createSoftware = async (connector, body) => {
  const response = await connector.client.post('/api/v1/smart-asset/software', body);
  return response.data;
};

export const updateSoftware = async (connector, softwareId, body) => {
  const response = await connector.client.patch(`/api/v1/smart-asset/software/${softwareId}`, body);
  return response.data;
};

export const deleteSoftware = async (connector, softwareId) => {
  const response = await connector.client.delete(`/api/v1/smart-asset/software/${softwareId}`);
  return response.data;
};
