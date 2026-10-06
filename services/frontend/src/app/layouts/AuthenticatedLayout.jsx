import { Fragment, useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import { WiniBox, WiniFormContextProvider, Main, WiniTypography } from '@/shared/ui';
import { NavigationDrawer, PageHeader, useAuthenticatedLayout } from '@/features/navigation/authenticated-layout';
import { DynamicRouteContent } from '@/features/route';
import { NotFoundPage } from '@/pages/not-found';
import { useAuthenticatedLayoutDrawerState } from './useAuthenticatedLayoutDrawerState';

// dayjs 전역 설정
import '@/shared/config';

const MENU_PERMISSION_KEYS = [
  'selectStatus',
  'insertStatus',
  'updateStatus',
  'deleteStatus',
  'printStatus',
  'downStatus',
  'manageStatus',
  'custom1Status',
  'custom2Status',
  'custom3Status',
];

const isNoMenuPermission = (menuAuthInfo) => {
  if (!menuAuthInfo) {
    return false;
  }

  return MENU_PERMISSION_KEYS.every((key) => menuAuthInfo[key] === 'NONE');
};

const findFirstProgramMapping = (menus, excludedMenuIds = []) => {
  if (!menus || menus.length === 0) {
    return '';
  }

  const excludedIdSet = new Set(excludedMenuIds.filter(Boolean));

  for (const item of menus) {
    if (item.menuType === 'PROGRAM' && !excludedIdSet.has(item.id)) {
      return item.programMapping ?? '';
    }

    if (item.children?.length > 0) {
      const childProgramMapping = findFirstProgramMapping(item.children, excludedMenuIds);

      if (childProgramMapping) {
        return childProgramMapping;
      }
    }
  }

  return '';
};

/**
 * AuthenticatedLayout
 * - Drawer + Header + Main 영역. 동적 라우트 콘텐츠 렌더링
 * - errorFallbackComponent, getLazyFallback는 app에서 주입
 */
export function AuthenticatedLayout({
  errorFallbackComponent,
  getLazyFallback,
}) {
  const [deniedMenuIds, setDeniedMenuIds] = useState([]);
  const location = useLocation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const {
    menulist,
    selectedMenu,
    menuUrl,
    breadcrumb,
    title,
    isMenuLoaded,
    isPathResolved,
    handleMenuClick,
    handleMenuSelect,
    navigateToProgram,
    logout,
    openMenu,
    closeAllMenu,
    menuAuthInfo,
    parentParams,
    handleParams,
    contextValueState,
  } = useAuthenticatedLayout();

  const {
    isDrawerOpen,
    handleToggleDrawer,
    handleOpenDrawer,
    handleCloseDrawer,
    handleMenuClickWithDrawer,
    handleMenuSelectWithDrawer,
  } = useAuthenticatedLayoutDrawerState({
    openMenu,
    closeAllMenu,
    handleMenuClick,
    handleMenuSelect,
    isMobile,
  });

  useEffect(() => {
    if (!selectedMenu || !menuAuthInfo) {
      return;
    }

    if (isNoMenuPermission(menuAuthInfo)) {
      setDeniedMenuIds((prev) => (
        prev.includes(selectedMenu) ? prev : [...prev, selectedMenu]
      ));
      return;
    }

    setDeniedMenuIds((prev) => prev.filter((menuId) => menuId !== selectedMenu));
  }, [selectedMenu, menuAuthInfo]);

  const isDeniedCurrentMenu = selectedMenu && isNoMenuPermission(menuAuthInfo);
  const isRootPath = location.pathname === '/';
  const isUnmatchedPath = !isRootPath && !menuUrl && !selectedMenu;

  const fallbackProgramMapping = isPathResolved && (isRootPath || isUnmatchedPath)
    ? findFirstProgramMapping(menulist.origin, deniedMenuIds)
    : '';

  const shouldShowEmptyShell = isMenuLoaded && isPathResolved && (
    (isRootPath || isUnmatchedPath) &&
    !fallbackProgramMapping
  );

  useEffect(() => {
    if (!isMenuLoaded || !fallbackProgramMapping) {
      return;
    }

    if (fallbackProgramMapping !== menuUrl) {
      navigateToProgram(fallbackProgramMapping);
    }
  }, [isMenuLoaded, fallbackProgramMapping, menuUrl, navigateToProgram]);


  return (
    <Fragment>
      <NavigationDrawer
        menulist={menulist}
        selectedMenu={selectedMenu}
        hiddenMenuIds={deniedMenuIds}
        onMenuClick={handleMenuClickWithDrawer}
        onMenuSelect={handleMenuSelectWithDrawer}
        onLogout={logout}
        onToggleDrawer={handleToggleDrawer}
        onCloseDrawer={handleCloseDrawer}
        isOpen={isDrawerOpen}
        isMobile={isMobile}
      />

      <PageHeader
        title={title}
        breadcrumb={breadcrumb}
        menuUrl={menuUrl}
        isDrawerOpen={isDrawerOpen}
        isMobile={isMobile}
        onOpenDrawer={handleOpenDrawer}
      />

      <Main open={isDrawerOpen} mobile={isMobile}>
        <WiniFormContextProvider value={contextValueState}>
          {isDeniedCurrentMenu ? (
            <NotFoundPage mode="menu" />
          ) : shouldShowEmptyShell ? (
            <WiniBox className="flex h-full min-h-[50vh] flex-col items-center justify-center gap-2">
              <WiniTypography variant="h5">접근 가능한 메뉴가 없습니다.</WiniTypography>
              <WiniTypography className="text-gray-600">
                권한을 다시 확인해주세요.
              </WiniTypography>
            </WiniBox>
          ) : (
            <DynamicRouteContent
              menuUrl={menuUrl}
              selectedMenu={selectedMenu}
              isMenuLoaded={isMenuLoaded}
              menuAuthInfo={menuAuthInfo}
              navigateToProgram={navigateToProgram}
              parentParams={parentParams}
              handleParams={handleParams}
              errorFallbackComponent={errorFallbackComponent}
              getLazyFallback={getLazyFallback}
            />
          )}
        </WiniFormContextProvider>
      </Main>
    </Fragment>
  );
}
