import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { login } from '../api/api';
import { authAdapter } from '@/shared/adapters';
import loginData from '@/shared/adapters/data/login.json';
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

  const { id, password } = inputs;

  /**
   * 로그인 실행
   */
  const handleLogin = async (organizationId = null) => {
    setIsLoading(true);

    try {
      const data = authAdapter.toLoginResult(
        await login(id, password, organizationId),
        // loginData,
      );
      const authState = authAdapter.toAuthState(data);

      if (authState.isAuthenticated) {
        const userData = data.data;
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
            orgId: userData.userOrganizationList[0].organizationId,
            autId: userData.userOrganizationList[0].userGroupId,
            username: userData.fullName,
            name: userData.fullName,
            userId: userData.userId,
          },
        });
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
  };
};
