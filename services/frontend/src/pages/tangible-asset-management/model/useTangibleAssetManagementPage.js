import { useCallback, useState } from 'react';
import { useTangibleAssetList } from '@/features/tangibleAsset/list';
import {
  useTangibleAssetActions,
  useTangibleAssetEditor,
  useTangibleAssetOptions,
} from '@/features/tangibleAsset/manage';
import { useAssignmentHistory } from '@/features/tangibleAsset/assignment';
import { useDisuse } from '@/features/tangibleAsset/disuse';
import { useBulkActions } from '@/features/tangibleAsset/bulk';
import { useQrLabels } from '@/features/tangibleAsset/qrLabel';
import { useExcelUpsert } from '@/features/tangibleAsset/excelUpsert';
import {
  useAssetHistory,
  useActivityLog,
} from '@/features/tangibleAsset/history';

export const useTangibleAssetManagementPage = () => {
  const {
    tangibleAssetList,
    pageInfo,
    isLoading: isListLoading,
    error: listError,
    fetchTangibleAssetList,
  } = useTangibleAssetList();
  const { create, update } = useTangibleAssetActions();
  const {
    categoryList,
    locationList,
    userList,
    isLoading: isOptionsLoading,
    isReady: isOptionsReady,
    error: optionsError,
    loadOptions,
  } = useTangibleAssetOptions();

  const [searchDraft, setSearchDraft] = useState({ keyword: '' });
  const [searchApplied, setSearchApplied] = useState({ keyword: '' });

  const onSearchChange = useCallback((e) => {
    const { name, value } = e.target;
    setSearchDraft((prev) => ({ ...prev, [name]: value }));
  }, []);

  // 검색은 서버 사이드로 바뀌었으므로 새 키워드로 찾을 때는 항상 첫 페이지(0)부터 다시 조회한다
  const onSelect = useCallback(async () => {
    const keyword = (searchDraft.keyword || '').trim();
    setSearchApplied({ keyword });
    await fetchTangibleAssetList({ keyword, page: 0 });
  }, [searchDraft, fetchTangibleAssetList]);

  const onPageChange = useCallback(
    async (nextPage) => {
      await fetchTangibleAssetList({
        keyword: searchApplied.keyword,
        page: nextPage,
      });
    },
    [fetchTangibleAssetList, searchApplied],
  );

  // 최초 1회 로드는 useTangibleAssetList 내부(useInitialFetch)에서 처리한다
  // 서버가 keyword/page 단위로 이미 필터링·페이징해서 내려주므로 클라이언트에서 다시 거를 필요가 없다
  const dataListView = tangibleAssetList;

  const refreshCurrentList = useCallback(
    () =>
      fetchTangibleAssetList({
        keyword: searchApplied.keyword,
        page: pageInfo.currentPage,
      }),
    [fetchTangibleAssetList, searchApplied.keyword, pageInfo.currentPage],
  );

  const editor = useTangibleAssetEditor({
    fetchList: refreshCurrentList,
    create,
    update,
    isOptionsReady,
  });

  const selectedAssetId = editor.formData.tangibleAssetId;
  const setSelectedAsset = editor.setSelected;
  const handleAssignmentReleased = useCallback(async () => {
    const data = await refreshCurrentList();
    const updated = (data?.data?.content || []).find(
      (x) => x.tangibleAssetId === selectedAssetId,
    );
    if (updated) setSelectedAsset(updated);
  }, [refreshCurrentList, selectedAssetId, setSelectedAsset]);

  const assignment = useAssignmentHistory(
    editor.formData.tangibleAssetId,
    handleAssignmentReleased,
  );
  const assetHistory = useAssetHistory(editor.formData.tangibleAssetId);
  const activityLog = useActivityLog();

  // 불용 처리(S-241) 후에도 배정 회수와 동일하게 목록을 새로고침하고 선택된 편집 패널을 최신 상태로 맞춘다
  const disuse = useDisuse({
    fetchList: refreshCurrentList,
    onDone: handleAssignmentReleased,
  });

  const [checkedRows, setCheckedRows] = useState([]);

  const clearCheckedRows = useCallback(() => setCheckedRows([]), []);

  const bulk = useBulkActions({
    checkedRows,
    fetchList: refreshCurrentList,
    onDone: clearCheckedRows,
    canBatchUpdate: isOptionsReady,
  });

  const qrLabel = useQrLabels();
  const printLabels = qrLabel.printLabels;
  const printQrLabels = useCallback(
    () => printLabels(checkedRows),
    [printLabels, checkedRows],
  );

  const excelUpsert = useExcelUpsert({ fetchList: refreshCurrentList });

  return {
    dataListView,
    pageInfo,
    isListLoading,
    listError,
    isOptionsLoading,
    isOptionsReady,
    optionsError,
    reloadOptions: loadOptions,
    onPageChange,
    searchData: searchDraft,
    onSearchChange,
    onSelect,
    editor,
    assignment,
    disuse,
    assetHistory,
    activityLog,
    checkedRows,
    onCheckSelectionChange: setCheckedRows,
    bulk,
    qrLabel,
    printQrLabels,
    excelUpsert,
    categoryList,
    locationList,
    userList,
  };
};
