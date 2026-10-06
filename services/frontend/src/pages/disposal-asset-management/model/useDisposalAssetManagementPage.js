import { useCallback, useState } from 'react';
import { useDisposalAssetList } from '@/features/disposalAsset/list';
import { useDisposalActions } from '@/features/disposalAsset/actions';

export const useDisposalAssetManagementPage = () => {
  const {
    disposalAssetList,
    pageInfo,
    isLoading: isListLoading,
    error: listError,
    fetchDisposalAssetList,
  } = useDisposalAssetList();

  const [searchDraft, setSearchDraft] = useState({ lifeStatus: '' });
  const [searchApplied, setSearchApplied] = useState({ lifeStatus: '' });
  const [selectedRow, setSelectedRow] = useState(null);

  const onSearchChange = useCallback((e) => {
    const { name, value } = e.target;
    setSearchDraft((prev) => ({ ...prev, [name]: value }));
  }, []);

  const refreshCurrentList = useCallback(
    () =>
      fetchDisposalAssetList({
        lifeStatus: searchApplied.lifeStatus,
        page: pageInfo.currentPage,
      }),
    [fetchDisposalAssetList, searchApplied.lifeStatus, pageInfo.currentPage],
  );

  // 복귀·처분 처리 후 선택된 행을 새로 받은 목록 기준으로 다시 찾는다 - 복귀는 필터에 따라
  // 목록에서 사라질 수 있고(더 이상 불용/처분완료가 아니므로), 그 경우 선택도 함께 해제된다.
  const resyncSelection = useCallback((freshList) => {
    const content = freshList?.data?.content || [];
    setSelectedRow(
      (prev) =>
        content.find((row) => row.tangibleAssetId === prev?.tangibleAssetId) ||
        null,
    );
  }, []);

  const actions = useDisposalActions({
    fetchList: refreshCurrentList,
    onDone: resyncSelection,
  });
  const { resetDisposeForm } = actions;
  const isActionRunning = actions.isRestoring || actions.isDisposing;

  const clearSelection = useCallback(() => {
    setSelectedRow(null);
    resetDisposeForm();
  }, [resetDisposeForm]);

  const onRowSelect = useCallback(
    (row) => {
      if (isActionRunning) return;

      const nextRow = row || null;
      if (selectedRow?.tangibleAssetId !== nextRow?.tangibleAssetId) {
        resetDisposeForm();
      }
      setSelectedRow(nextRow);
    },
    [isActionRunning, resetDisposeForm, selectedRow?.tangibleAssetId],
  );

  // 필터나 페이지가 바뀌면 이전 목록의 선택과 처분 입력값을 함께 해제한다.
  const onSelect = useCallback(async () => {
    if (isActionRunning) return;

    const lifeStatus = searchDraft.lifeStatus || '';
    clearSelection();
    setSearchApplied({ lifeStatus });
    await fetchDisposalAssetList({ lifeStatus, page: 0 });
  }, [
    clearSelection,
    fetchDisposalAssetList,
    isActionRunning,
    searchDraft.lifeStatus,
  ]);

  const onPageChange = useCallback(
    async (nextPage) => {
      if (isActionRunning) return;

      clearSelection();
      await fetchDisposalAssetList({
        lifeStatus: searchApplied.lifeStatus,
        page: nextPage,
      });
    },
    [
      clearSelection,
      fetchDisposalAssetList,
      isActionRunning,
      searchApplied.lifeStatus,
    ],
  );

  return {
    disposalAssetList,
    pageInfo,
    isListLoading,
    listError,
    searchData: searchDraft,
    onSearchChange,
    onSelect,
    onPageChange,
    selectedRow,
    onRowSelect,
    isActionRunning,
    actions,
  };
};
