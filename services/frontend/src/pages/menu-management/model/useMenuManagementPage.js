import { useCallback, useEffect, useRef, useState } from 'react';
import {
  useMenuTree,
  useMenuEditor,
  useMenuOrder,
  useMenuTreeSelection,
} from '@/features/menu/manage';
import { useProgramList, useProgramEditor } from '@/features/program/manage';
import { useMenuProgramMapping } from '@/features/menu/program-mapping';
import { winiGrid } from '@/shared/lib';

/**
 * 메뉴 관리 페이지 비즈니스 로직 Hook
 * 섹션 간 상호작용(매핑)과 페이지 레벨 상태만 관리
 */
export const useMenuManagementPage = (props) => {
  const [isSmallScreen, setIsSmallScreen] = useState(false);
  const [searchMenu, setSearchMenu] = useState('');
  const [relDialogOpen, setRelDialogOpen] = useState(false);
  const programGridRef = useRef(null);
  const pendingProgramSelectionIdRef = useRef(null);

  // 페이지 레벨 성공 콜백
  const onSuccess = useCallback(() => {
    try {
      if (props.getParams) props.getParams.reLoad();
    } catch (e) {}
  }, [props.getParams]);

  // 메뉴 관리
  const {
    menu,
    menuOnly,
    selectedNode,
    setMenu,
    loadMenuTree,
    selectNode,
    searchMenu: performSearchMenu,
  } = useMenuTree();
  const {
    menuData,
    setMenuInfo,
    handleChange: handleMenuChange,
    createMenu,
    updateMenu,
    deleteMenu,
    resetMenuData,
  } = useMenuEditor(() => {
    onSuccess();
  });
  const { saveMenuOrder } = useMenuOrder(() => {
    onSuccess();
  });
  const { checkedProgramIds, toggleNodeCheck } = useMenuTreeSelection();

  // 프로그램 관리
  const {
    programs,
    searchKeyword,
    setSearchKeyword,
    loadPrograms,
    searchPrograms,
    getCheckedPrograms,
  } = useProgramList();
  const {
    programData,
    loadProgramDetail,
    handleChange: handleProgramChange,
    addRelatedProgram,
    removeRelatedProgram,
    createProgram,
    updateProgram,
    deleteProgram,
    resetProgramData,
  } = useProgramEditor(() => {
    onSuccess();
  });

  // 메뉴-프로그램 매핑 hooks
  const { removeProgramsFromMenu, addProgramsToMenu } = useMenuProgramMapping(onSuccess);

  // 미디어 쿼리
  useEffect(() => {
    const media = window.matchMedia('(max-width: 900px)');
    setIsSmallScreen(media.matches);

    const listener = () => setIsSmallScreen(media.matches);
    media.addEventListener('change', listener);
    return () => media.removeEventListener('change', listener);
  }, []);

  // 초기 데이터 로드
  useEffect(() => {
    loadMenuTree();
    loadPrograms();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // 메뉴 선택 시 에디터 동기화
  useEffect(() => {
    if (selectedNode) {
      setMenuInfo(selectedNode);
    }
  }, [selectedNode, setMenuInfo]);

  const handleToggleNodeCheck = (id, isChecked, node) => {
    toggleNodeCheck(menu, setMenu, id, isChecked, node);
  };

  const handleMenuTreeChange = useCallback(
    (nextMenu) => {
      setMenu((prevMenu) => {
        if (prevMenu === nextMenu) return prevMenu;

        // WiniTreeView가 동일 트리를 새 참조로 연속 전달하는 경우 렌더 루프를 방지한다.
        try {
          if (JSON.stringify(prevMenu) === JSON.stringify(nextMenu)) {
            return prevMenu;
          }
        } catch (e) {}

        return nextMenu;
      });
    },
    [setMenu],
  );

  const handleSearchMenu = () => {
    performSearchMenu(searchMenu);
  };

  const handleSaveOrder = () => {
    saveMenuOrder(menu);
  };

  const handleInsertMenu = () => {
    createMenu(loadMenuTree);
  };

  const handleUpdateMenu = () => {
    updateMenu(loadMenuTree);
  };

  const handleDeleteMenu = () => {
    deleteMenu(loadMenuTree);
  };

  const onSelectionProgramGrid = (e) => {
    const row = e.api.getSelectedRows()[0];
    if (!row) return;
    if (row.programId) {
      loadProgramDetail(row.programId);
    }
  };

  const requestProgramReselect = useCallback((programId) => {
    if (!programId) return;
    pendingProgramSelectionIdRef.current = programId;
  }, []);

  useEffect(() => {
    const pendingId = pendingProgramSelectionIdRef.current;
    if (!pendingId || programs.length === 0) return;

    let retry = 0;
    let timerId = null;

      const selected = winiGrid.reselectAfterDataLoad(programGridRef, pendingId, {
        idField: 'programId',
        moveToPage: true,
        selectDelayMs: 0,
      });

      if (selected) {
        pendingProgramSelectionIdRef.current = null;
        return;
      }

  }, [programs]);

  const handleInsertProgram = () => {
    createProgram(async (id) => {
      await loadPrograms();
      requestProgramReselect(id);
    });
  };

  const handleUpdateProgram = () => {
    const currentProgramId = programData.programId;
    updateProgram(async (id) => {
      await loadPrograms();
      requestProgramReselect(id || currentProgramId);
    });
  };

  const handleDeleteProgram = () => {
    deleteProgram(async () => {
      await loadPrograms();
    });
  };

  const handleOpenRelDialog = useCallback(() => {
    setRelDialogOpen((prev) => !prev);
  }, []);

  const handleSelectRelProgram = useCallback(
    (program) => {
      addRelatedProgram(program);
    },
    [addRelatedProgram],
  );

  // 메뉴에서 프로그램 삭제
  const handleRemovePrograms = () => {
    removeProgramsFromMenu(checkedProgramIds, loadMenuTree);
  };

  // 메뉴에 프로그램 추가
  const handleAddPrograms = () => {
    const checkedPrograms = getCheckedPrograms();
    addProgramsToMenu(
      selectedNode,
      checkedPrograms,
      loadMenuTree,
    );
  };

  return {
    isSmallScreen,
    // 메뉴
    searchMenu,
    setSearchMenu,
    menu,
    menuOnly,
    setMenu,
    handleMenuTreeChange,
    menuData,
    checkedProgramIds,
    selectNode,
    handleToggleNodeCheck,
    handleSearchMenu,
    handleSaveOrder,
    handleInsertMenu,
    handleUpdateMenu,
    handleDeleteMenu,
    handleMenuChange,
    resetMenuData,
    // 프로그램
    programs,
    programGridRef,
    programData,
    searchKeyword,
    setSearchKeyword,
    searchPrograms,
    onSelectionProgramGrid,
    handleInsertProgram,
    handleUpdateProgram,
    handleDeleteProgram,
    handleProgramChange,
    removeRelatedProgram,
    resetProgramData,
    relDialogOpen,
    handleOpenRelDialog,
    handleSelectRelProgram,
    // 매핑
    handleRemovePrograms,
    handleAddPrograms,
  };
};
