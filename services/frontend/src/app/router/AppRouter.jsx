import { Fragment, lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthenticatedLayout } from '@/app/layouts';
import { AuthErrorPage } from '@/pages/auth-error';
import { LoginPage } from '@/pages/login';
import { NotFoundPage } from '@/pages/not-found';

import { AuthGuard, GuestGuard } from '@/features/auth/guard';
import { QrScannerPage, AssetScanDetailPage } from '@/pages/asset-scan';
import { ENV } from '@/shared/config';

/** 동적 라우트 에러 시 폴백용. 참조 안정적으로 app에서 한 번만 정의 */
const getLazyFallback = () => import('@/pages/auth-error');

/**
 * 개발 전용 페이지(/dev, /sample, /ui-builder)는 정적 import하지 않는다 - 정적 import는
 * import.meta.env.DEV 분기 밖에 있어도 모듈 전체(및 그 안의 import.meta.glob 등)가 운영
 * 번들에 그대로 포함되기 때문이다. import.meta.env.DEV는 ENV.IS_DEV(런타임에 .env의
 * VITE_NODE_ENV 값을 읽음)와 달리 Vite가 빌드 커맨드 자체로 고정하는 진짜 빌드타임 상수라,
 * "false ? A : B" 형태의 삼항연산자를 쓰면 운영 빌드에서 A(동적 import 포함)가 데드코드로
 * 완전히 제거된다(실제 프로덕션 빌드로 재현 검증 완료).
 */
const DevPage = import.meta.env.DEV
  ? lazy(() => import('@/pages/dev').then((m) => ({ default: m.DevPage })))
  : null;
const SampleMain = import.meta.env.DEV
  ? lazy(() => import('@/pages/wini-samples').then((m) => ({ default: m.SampleMain })))
  : null;
const UiBuilderPage = import.meta.env.DEV
  ? lazy(() => import('@/pages/ui-builder').then((m) => ({ default: m.UiBuilderPage })))
  : null;

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 고정 라우트 (인증 불필요) */}
        <Route path="/login" element={<GuestGuard><LoginPage /></GuestGuard>} />
        {
          // 개발 환경이고 로컬 환경이면 개발 페이지 라우트. DevPage가 null이면(운영 빌드)
          // 이 라우트 자체가 등록되지 않는다.
          DevPage && ENV.IS_DEV && ENV.IS_LOCALHOST && (
            <Route path="/dev" element={<Suspense fallback={null}><DevPage /></Suspense>} />
          )
        }
        {
          // 컴포넌트 샘플/UI 빌더도 인증 없이 접근 가능한 개발 도구이므로 /dev와 동일하게 제한한다.
          SampleMain && ENV.IS_DEV && ENV.IS_LOCALHOST && (
            <Route path="/sample" element={<Suspense fallback={null}><SampleMain /></Suspense>} />
          )
        }
        {
          UiBuilderPage && ENV.IS_DEV && ENV.IS_LOCALHOST && (
            <Route path="/ui-builder" element={<Suspense fallback={null}><UiBuilderPage /></Suspense>} />
          )
        }

        {/* 인증 필요 라우트 - 동적 메뉴 */}
        <Route element={<AuthGuard />}>
          <Route
            path="/"
            element={
              <AuthenticatedLayout
                errorFallbackComponent={AuthErrorPage}
                getLazyFallback={getLazyFallback}
              />
            }
          >
            <Route index element={<Fragment />} />
            <Route path=":menuPath" element={<Fragment />} />
          </Route>

          {/* QR 스캔 - 사이드바 메뉴 밖의 단독 페이지 (S-216/217) */}
          <Route path="/asset-scan" element={<QrScannerPage />} />
          <Route path="/asset-scan/:tangibleAssetId" element={<AssetScanDetailPage />} />
        </Route>

        <Route path="/*" element={<NotFoundPage mode="404" />} />
      </Routes>
    </BrowserRouter>
  );
}
