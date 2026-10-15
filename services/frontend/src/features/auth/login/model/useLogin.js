import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { login, secondAuth } from '../api/api';
import { authAdapter } from '@/shared/adapters';
import { winiMsg } from '@/shared/model';

/**
 * 로그인 로직을 처리하는 커스텀 훅
 */
export const useLogin = () => {
  const navigate = useNavigate();
  const location = useLocation();
  // AuthGuard가 미인증 접근을 막으면서 넘겨준 원래 요청 경로(예: QR 스캔 상세 딥링크)로,
  // 로그인 성공 후 복귀시키기 위함. 없으면 기존과 동일하게 홈으로 이동.
  const returnTo = location.state?.from || '/';
  const [inputs, setInputs] = useState({
    id: '',
    password: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  // null이 아니면 1차 인증(아이디/비밀번호)은 통과했고 OTP 코드 입력을 기다리는 상태
  const [mfaChallengeToken, setMfaChallengeToken] = useState(null);

  const { id, password } = inputs;

  /**
   * 로그인(또는 2차 인증) 응답이 최종 성공일 때 공통으로 처리 - 토큰 저장 + 이동
   */
  const completeLogin = (userData, authState) => {
    const org = userData.userOrganizationList[0];

    // Communicator가 생성될 때 헤더에 필요한 값들을 미리 저장
    if (authState.authMode === 'jwt' && authState.accessToken) {
      localStorage.setItem('jwtsessiontoken', authState.accessToken);
    } else {
      localStorage.removeItem('jwtsessiontoken');
    }
    localStorage.setItem('OrgId', org.organizationId);
    localStorage.setItem('UserId', userData.userId);
    localStorage.setItem('UserName', userData.fullName);
    localStorage.setItem('GroupId', org.userGroupId);

    navigate(returnTo, {
      state: {
        orgId: org.organizationId,
        autId: org.userGroupId,
        username: userData.fullName,
        name: userData.fullName,
        userId: userData.userId,
      },
    });
  };

  /**
   * 로그인 실행
   */
  const handleLogin = async (organizationId = null) => {
    setIsLoading(true);

    try {
      const data = authAdapter.toLoginResult(
        await login(id, password, organizationId),
      );
      const authState = authAdapter.toAuthState(data);

      if (authState.isAuthenticated) {
        completeLogin(data.data, authState);
      } else if (authState.authMode === 'mfa_required' && authState.mfaChallengeToken) {
        // 비밀번호는 맞았고 OTP 앱 코드 입력만 남은 상태 - 화면을 코드 입력으로 전환
        setMfaChallengeToken(authState.mfaChallengeToken);
      } else {
        // authState.isAuthenticated는 authMode가 jwt(토큰 존재)이거나 session일 때만 true다.
        // 여기 도달한다는 것은 authMode가 unknown, 즉 로그인 응답에서 인증 방식을 식별하지
        // 못했다는 뜻이므로 실제 로그인 성공이 아니다 - 실패로 처리하고 더 진행하지 않는다.
        winiMsg.showAlert('로그인 처리 중 오류가 발생했습니다. 다시 시도해주세요.');
      }

    } catch (error) {
      if (
        error.response?.data?.errorCodeName === 'COMMON_RESTAPI_ENC_REQUIRED' &&
        error.response?.data?.shouldRetry
      ) {
        await handleLogin(organizationId);
      } else if (error.response) {
        winiMsg.showAlert(error.response.data.message);
      } else {
        winiMsg.showAlert('아이디와 비밀번호를 확인해주세요.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * OTP 앱 6자리 코드(또는 백업코드) 검증 - MFA 2단계
   */
  const handleVerifyMfaCode = async (code) => {
    if (!mfaChallengeToken) return;

    setIsLoading(true);

    try {
      const data = authAdapter.toLoginResult(await secondAuth(mfaChallengeToken, code));
      const authState = authAdapter.toAuthState(data);

      if (authState.isAuthenticated) {
        completeLogin(data.data, authState);
      } else {
        winiMsg.showAlert('인증 코드가 올바르지 않거나 만료되었습니다.');
      }
    } catch (error) {
      if (error.response) {
        winiMsg.showAlert(error.response.data?.message || '인증 코드를 확인해주세요.');
      } else {
        winiMsg.showAlert('인증 코드를 확인해주세요.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * OTP 코드 입력 화면에서 뒤로가기 - 아이디/비밀번호 입력으로 되돌아간다
   */
  const cancelMfa = () => {
    setMfaChallengeToken(null);
  };

  /**
   * 입력값 변경 핸들러
   */
  const onInputsChange = (e) => {
    const { name, value } = e.target;
    setInputs({
      ...inputs,
      [name]: value,
    });
  };

  /**
   * 입력값 초기화
   */
  const onInputsReset = () => {
    setInputs({
      id: '',
      password: '',
    });
  };

  /**
   * Enter 키 입력 핸들러
   */
  const onKeyDownPassword = (e) => {
    if (e.keyCode === 13) {
      handleLogin();
    }
  };

  return {
    id,
    password,
    isLoading,
    onInputsChange,
    onInputsReset,
    onKeyDownPassword,
    handleLogin,
    isMfaRequired: !!mfaChallengeToken,
    handleVerifyMfaCode,
    cancelMfa,
  };
};
