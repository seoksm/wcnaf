import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniGridLayout, WiniGridItem } from '@/shared/ui/wini';
import { KakaoTestActions, useKakaoTest } from '@/features/auth/kakao-test';

export default function KakaoTestPage() {
  const { status, user, error, isLoading, handleLogin, handleUser, handleLogout } = useKakaoTest();

  return (
    <WiniFormNormal>
      <WiniBox className="mx-auto flex min-h-screen w-full max-w-5xl flex-col gap-6 py-8">
        <h1 className="text-3xl font-bold">카카오 로그인 테스트</h1>
        <WiniBox ui="info" className="space-y-3 break-all">
          <p>개발 서버 전용입니다. REST API 방식으로 로그인하고 토큰은 서버에서 보관합니다.</p>
          <p>프로젝트 계정 로그인과 별도로 동작하며, 테스트 세션은 최대 30분 동안 유지됩니다.</p>
          <p>.env.local에 KAKAO_TEST_ENABLED, KAKAO_TEST_ORIGIN, KAKAO_TEST_REST_API_KEY를 설정하세요. Client secret 사용 시 KAKAO_TEST_CLIENT_SECRET도 입력하세요.</p>
          <p>카카오 앱의 로그인 기능을 활성화하고 아래 Redirect URI를 등록하세요.</p>
          <p>Redirect URI: <strong>{status?.redirectUri || '설정을 완료하면 표시됩니다.'}</strong></p>
        </WiniBox>
        <WiniGridLayout container rowSpacing={2}>
          <WiniGridItem xs={12}>
            <WiniBox ui="line" role="status" aria-live="polite">
              {isLoading ? '처리 중입니다…' : status?.authenticated ? '로그인되었습니다. 사용자 정보를 조회할 수 있습니다.' : '로그인이 필요합니다.'}
            </WiniBox>
          </WiniGridItem>
          {error && <WiniGridItem xs={12}><WiniBox ui="info" role="alert">{error}</WiniBox></WiniGridItem>}
        </WiniGridLayout>
        <KakaoTestActions authenticated={Boolean(status?.authenticated)} disabled={isLoading || !status} onLogin={handleLogin} onUser={handleUser} onLogout={handleLogout} />
        <WiniBox ui="form" className="min-h-48" aria-busy={isLoading}>
          <h2 className="mb-4 text-xl font-bold">사용자 정보</h2>
          {user ? <pre className="overflow-auto whitespace-pre-wrap break-all">{JSON.stringify(user, null, 2)}</pre> : <p>조회된 사용자 정보가 없습니다.</p>}
        </WiniBox>
      </WiniBox>
    </WiniFormNormal>
  );
}
