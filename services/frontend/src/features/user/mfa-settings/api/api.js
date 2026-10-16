/**
 * user 도메인 - 본인 MFA(TOTP) 등록/해제 API
 */

export const enrollMfa = async (connector) => {
  const response = await connector.client.post('/api/v1/system/user/mfa/enroll');
  return response.data;
};

export const confirmMfaEnroll = async (connector, code) => {
  const response = await connector.client.post('/api/v1/system/user/mfa/enroll/confirm', { code });
  return response.data;
};

export const disableMfa = async (connector, code) => {
  const response = await connector.client.post('/api/v1/system/user/mfa/disable', { code });
  return response.data;
};
