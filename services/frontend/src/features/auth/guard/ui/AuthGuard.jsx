import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { authAdapter } from '@/shared/adapters';

/**
 * AuthGuard
 * - 인증(JWT) 없으면 /login으로 리다이렉트 (복귀 경로를 state.from으로 함께 전달)
 * - 있으면 하위 라우트를 렌더링
 */
export function AuthGuard() {
  const authState = authAdapter.getAuthStateFromStorage();
  const location = useLocation();

  if (!authState.isAuthenticated) {
    return <Navigate to="/login" state={{ from: `${location.pathname}${location.search}` }} replace />;
  }

  return <Outlet />;
}
