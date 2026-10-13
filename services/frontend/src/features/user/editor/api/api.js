/**
 * 사용자 편집(CRUD/승인/비번관리) 유즈케이스 API
 */
export {
  fetchUser,
  createUser,
  updateUser,
  deleteUser,
} from '@/entities/user';

/**
 * 가입 승인
 */
export const acceptUserJoin = async (connector, userId) => {
  const response = await connector.client.patch(
    `/api/v1/system/user/${userId}/acceptJoin`,
    {}
  );
  return response.data;
};

/**
 * 비밀번호 초기화
 */
export const resetUserPassword = async (connector, userId, passwordInfo) => {
  const response = await connector.client.patch(
    `/api/v1/system/user/${userId}/resetPassword`,
    passwordInfo
  );
  return response.data;
};

/**
 * 비밀번호 오류 횟수 초기화 (로그인 잠금 해제)
 */
export const unlockUserLogin = async (connector, params) => {
  const response = await connector.client.patch('/api/v1/system/user/unlockLogin', params);
  return response.data;
};

/**
 * MFA(TOTP) 백업코드 재발급 - 기기 분실 등으로 본인 요청을 받은 관리자가 대신 처리한다.
 * 기존 백업코드는 전부 무효화되고 새 코드 10개가 평문으로 1회 반환된다.
 */
export const reissueUserMfaBackupCodes = async (connector, userId) => {
  const response = await connector.client.post(`/api/v1/system/user/mfa/admin/users/${userId}/backupCodes/reissue`);
  return response.data;
};
