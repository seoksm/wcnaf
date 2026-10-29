import { OpenCommunicator } from '@/shared/api';

const connector = new OpenCommunicator();
const prefix = '/__kakao-test';

export const getTestStatus = async () => {
  const response = await connector.client.get(`${prefix}/status`, { baseURL: window.location.origin });
  return response.data;
};

export const startTestLogin = async () => {
  const response = await connector.client.post(`${prefix}/start`, {}, { baseURL: window.location.origin });
  return response.data;
};

export const getTestUser = async () => {
  const response = await connector.client.post(`${prefix}/me`, {}, { baseURL: window.location.origin });
  return response.data;
};

export const logoutTest = async () => {
  const response = await connector.client.post(`${prefix}/logout`, {}, { baseURL: window.location.origin });
  return response.data;
};
