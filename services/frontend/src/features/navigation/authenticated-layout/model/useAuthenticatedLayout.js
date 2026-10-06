import { useUserInit, useLogout } from '@/entities/user';
import { userStore } from '@/shared/model';
import { useMenuTree } from './useMenuTree';
import { useMenuPermission } from './useMenuPermission';
import { useMainPageParams } from './useMainPageParams';
import { useMainPageContext } from './useMainPageContext';

/**
 * 인증된 레이아웃 비즈니스 로직 Hook
 */
export const useAuthenticatedLayout = () => {
  const { currentUserId } = useUserInit();

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
    openMenu,
    closeAllMenu,
    reloadMenu,
  } = useMenuTree(currentUserId);

  const { menuAuthInfo } = useMenuPermission(
    userStore.getUserId(),
    selectedMenu,
  );

  const { logout } = useLogout();

  const { parentParams, handleParams } = useMainPageParams(
    menuUrl,
    selectedMenu,
    menulist,
    reloadMenu,
  );

  const { contextValueState } = useMainPageContext(selectedMenu, menuAuthInfo);

  return {
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
    openMenu,
    closeAllMenu,
    reloadMenu,
    menuAuthInfo,
    logout,
    parentParams,
    handleParams,
    contextValueState,
  };
};
