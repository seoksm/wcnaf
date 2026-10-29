import { WiniBox, WiniButton } from '@/shared/ui/wini';

export const KakaoTestActions = ({ authenticated, disabled, onLogin, onUser, onLogout }) => (
  <WiniBox ui="btnbox" className="flex-wrap gap-2">
    <WiniBox ui="btnitem" className="flex-wrap gap-2">
      <WiniButton ui="default" disabled={disabled || authenticated} onClick={onLogin}>카카오 로그인</WiniButton>
      <WiniButton ui="line" disabled={disabled || !authenticated} onClick={onUser}>사용자 정보 조회</WiniButton>
      <WiniButton ui="line" disabled={disabled || !authenticated} onClick={onLogout}>테스트 로그아웃</WiniButton>
    </WiniBox>
  </WiniBox>
);
