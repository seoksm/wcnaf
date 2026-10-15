import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniGridLayout, WiniGridItem } from '@/shared/ui/wini';
import {
  MenuProgramSearchBar,
  MenuProgramGrid,
  MenuSelectedProgramInfo,
  MenuActionTypeFilter,
  MenuActionGrid,
  MenuActionEditor,
  useMenuProgramList,
  useMenuActionList,
  useMenuActionEditor,
} from '@/features/menu/manage-actions';
import { winiMsg } from '@/shared/model';
import React from 'react';

/**
 * 시스템 메뉴 액션 관리 페이지
 */
export const MenuRequestPermissionPage = (props) => {
  const formRef = React.useRef();

  // 프로그램 목록 관리
  const {
    listView,
    searchPg,
    selectedData,
    handleSearchChange,
    handleSearch,
    handleProgramSelect,
    loadProgramList,
  } = useMenuProgramList();

  // 액션 목록 관리
  const {
    refActionGrid,
    actionAllList,
    actionList,
    actionTypeFilter,
    setReloadId,
    handleFilterChange,
    loadActionList,
  } = useMenuActionList(selectedData.id);

  // 액션 편집 (등록/수정/삭제)
  const {
    // formRef,
    selectedActionData,
    handleActionSelect,
    handleChange,
    handleReset,
    handleSave,
    handleDelete,
  } = useMenuActionEditor(selectedData, (reloadId) => {
    loadActionList();
    if (reloadId) {
      setReloadId(reloadId);
    }
  },formRef);

  // 소스에서 가져오기 - 개발 환경에서만 동작한다. import() 호출이 반드시
  // "if (import.meta.env.DEV) { ... }" 블록 안에 있어야 Rollup이 운영 빌드에서
  // 이 분기 전체(= glob으로 번들링되는 전체 소스 원문 청크)를 제거한다 - early-return 형태로
  // 쓰면 번들러가 도달불가 코드로 보지 못해 그대로 포함되어 버리므로 이 구조를 유지할 것.
  const loadFromSource = async () => {
    if (import.meta.env.DEV) {
      const { loadActionsFromSource } = await import(
        '@/features/menu/manage-actions/model/useSourceLoader'
      );

      await loadActionsFromSource(selectedData, actionAllList, () => {
        loadActionList();
      });
    } else {
      winiMsg.showSnackbar('이 기능은 개발 환경에서만 사용할 수 있습니다.');
    }
  };

  return (
    <WiniFormNormal>
      {/* scrollFix: WiniGridItem에 y축 스크롤 가능 */}
      <WiniGridLayout container columnSpacing={2} scrollFix >
        {/*  scrollHidden: y축 스크롤 숨겨짐 */}
        <WiniGridItem scrollHidden>
          {/* 좌측: 프로그램 목록 */}
          <MenuProgramSearchBar
            searchValue={searchPg}
            onSearchChange={handleSearchChange}
            onSearch={handleSearch}
          />
          <MenuProgramGrid
            rowData={listView}
            onSelectionChanged={handleProgramSelect}
          />
        </WiniGridItem>

        {/* 우측: 액션 목록 및 편집 */}
        <WiniGridItem ratio={1.5}>
          <MenuSelectedProgramInfo
            programCode={selectedData.code}
            programName={selectedData.name}
          />

          <MenuActionTypeFilter
            value={actionTypeFilter}
            onChange={handleFilterChange}
          />

          <MenuActionGrid
            ref={refActionGrid}
            rowData={actionList}
            onSelectionChanged={handleActionSelect}
          />

          <MenuActionEditor
            ref={formRef}
            actionData={selectedActionData}
            selectedProgramId={selectedData.id}
            selectedProgramMapping={selectedData.mapping}
            selectedActionId={selectedActionData.programActionId}
            actionAllList={actionAllList}
            onChange={handleChange}
            onReset={handleReset}
            onSave={handleSave}
            onDelete={handleDelete}
            onLoadFromSource={loadFromSource}
          />
        </WiniGridItem>
      </WiniGridLayout>
    </WiniFormNormal>
  );
};

export default MenuRequestPermissionPage;
