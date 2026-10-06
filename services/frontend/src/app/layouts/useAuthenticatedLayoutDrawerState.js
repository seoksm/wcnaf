import { useCallback, useEffect, useRef, useState } from 'react';

/**
 * - AuthenticatedLayout 전용 UI 상태(Drawer open/close)와 핸들러
 * - 모바일(isMobile)에서는 기본적으로 닫혀 있고, 메뉴를 선택(navigate)하면 자동으로 닫힌다.
 */
export function useAuthenticatedLayoutDrawerState({ openMenu, closeAllMenu, handleMenuClick, handleMenuSelect, isMobile }) {
  const [isDrawerOpen, setIsDrawerOpen] = useState(!isMobile);
  const wasMobile = useRef(isMobile);

  // 데스크톱<->모바일 전환(창 크기 변경, 회전 등) 시에만 기본 상태로 재조정. 모바일 내에서의
  // 리렌더링마다 강제로 닫히면 사용자가 방금 연 드로어가 다시 닫혀버리므로 전환 시점에만 처리.
  useEffect(() => {
    if (wasMobile.current !== isMobile) {
      wasMobile.current = isMobile;
      setIsDrawerOpen(!isMobile);
    }
  }, [isMobile]);

  // 데스크톱 - 펼침/접힘(아이콘 레일) 토글. 접힐 때는 열려 있던 메뉴그룹도 같이 접는다.
  const handleToggleDrawer = useCallback(() => {
    setIsDrawerOpen((prev) => {
      const next = !prev;

      if (next) openMenu();
      else closeAllMenu();

      return next;
    });
  }, [openMenu, closeAllMenu]);

  // 모바일 - 햄버거 버튼으로 오버레이 드로어를 연다. 데스크톱의 메뉴그룹 펼침 상태와는
  // 무관하게 단순히 열기만 하도록 분리해, 토글 로직의 부수효과에 영향받지 않게 한다.
  const handleOpenDrawer = useCallback(() => {
    setIsDrawerOpen(true);
  }, []);

  const handleCloseDrawer = useCallback(() => {
    setIsDrawerOpen(false);
  }, []);

  const handleMenuClickWithDrawer = useCallback(
    (item) => {
      handleMenuClick(item, isDrawerOpen);
      if (!isDrawerOpen) setIsDrawerOpen(true);
    },
    [handleMenuClick, isDrawerOpen],
  );

  const handleMenuSelectWithDrawer = useCallback(
    (programMapping, id) => {
      handleMenuSelect(programMapping, id);
      // 모바일에서는 프로그램(실제 화면)을 선택하면 드로어를 덮고 있던 오버레이를 닫아
      // 방금 이동한 화면이 바로 보이게 한다.
      if (isMobile) setIsDrawerOpen(false);
    },
    [handleMenuSelect, isMobile],
  );

  return {
    isDrawerOpen,
    handleToggleDrawer,
    handleOpenDrawer,
    handleCloseDrawer,
    handleMenuClickWithDrawer,
    handleMenuSelectWithDrawer,
  };
}
