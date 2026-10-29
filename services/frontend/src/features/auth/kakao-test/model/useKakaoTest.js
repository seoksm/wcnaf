import { useEffect, useState } from 'react';
import { winiMsg } from '@/shared/model';
import { getTestStatus, startTestLogin, getTestUser, logoutTest } from '../api/api';

const getError = (error) => error.response?.data?.message || error.message || '요청에 실패했습니다.';

export const useKakaoTest = () => {
  const [status, setStatus] = useState(null);
  const [user, setUser] = useState(null);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    let active = true;
    const initialize = async () => {
      try {
        const result = await getTestStatus();
        if (active) {
          setStatus(result);
          setError(result.error || '');
        }
      } catch (err) {
        if (active) setError(getError(err));
      } finally {
        if (active) setIsLoading(false);
      }
    };
    void initialize();
    return () => { active = false; };
  }, []);

  const run = async (action) => {
    setIsLoading(true);
    setError('');
    try {
      await action();
    } catch (err) {
      const message = getError(err);
      setError(message);
      winiMsg.showSnackbar(message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogin = () => run(async () => {
    const result = await startTestLogin();
    window.location.assign(result.authorizeUrl);
  });

  const handleUser = () => run(async () => {
    setUser(null);
    try {
      setUser(await getTestUser());
      winiMsg.showSnackbar('사용자 정보를 조회했습니다.');
    } catch (err) {
      // 서버가 조회 실패 시 세션을 정리하므로 화면도 재로그인 상태로 맞춘다.
      setStatus((previous) => ({ ...previous, authenticated: false }));
      throw err;
    }
  });

  const handleLogout = () => run(async () => {
    const result = await logoutTest();
    setStatus((previous) => ({ ...previous, authenticated: false }));
    setUser(null);
    winiMsg.showSnackbar(result.message);
  });

  return { status, user, error, isLoading, handleLogin, handleUser, handleLogout };
};
