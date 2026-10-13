import { Communicator, requestWithEncryption } from '@/shared/api';

/**
 * 로그인 API
 */
export const login = async (username, password) => {
  const connector = new Communicator();
  connector.client.defaults.headers.common['Authorization'] = null;

  const payload = {
    ipSecurityStatus: 'DISABLE',
    username,
    password
  };

  const response = await requestWithEncryption(connector, {
    method: 'post',
    url: '/api/v1/system/user/login', // 일반 사용자 로그인용 엔드포인트
    data: payload,
  });

  return response.data;
};

/**
 * MFA(TOTP/백업코드) 2차 인증 - login()이 mfaRequired=true로 응답했을 때 호출한다.
 */
export const secondAuth = async (mfaChallengeToken, code) => {
  const connector = new Communicator();
  connector.client.defaults.headers.common['Authorization'] = null;

  const response = await requestWithEncryption(connector, {
    method: 'post',
    url: '/api/v1/system/user/secondAuth',
    data: { mfaChallengeToken, code },
  });

  return response.data;
};
