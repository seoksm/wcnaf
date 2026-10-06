import { useCallback, useMemo, useState } from 'react';
import { useAssetCategoryList } from '@/features/assetCategory/list';
import {
  useAssetCategoryActions,
  useAssetCategoryEditor,
} from '@/features/assetCategory/manage';

export const useAssetCategoryManagementPage = () => {
  const { assetCategoryList, isLoading, error, fetchAssetCategoryList } =
    useAssetCategoryList();
  const { create, update, remove } = useAssetCategoryActions();

  const [searchDraft, setSearchDraft] = useState({ keyword: '' });
  const [searchApplied, setSearchApplied] = useState({ keyword: '' });

  const onSearchChange = useCallback((e) => {
    const { name, value } = e.target;
    setSearchDraft((prev) => ({ ...prev, [name]: value }));
  }, []);

  const onSelect = useCallback(async () => {
    setSearchApplied({ keyword: (searchDraft.keyword || '').trim() });
    await fetchAssetCategoryList();
  }, [searchDraft, fetchAssetCategoryList]);

  // 최초 1회 로드는 useAssetCategoryList 내부(useInitialFetch)에서 처리한다

  const dataListView = useMemo(() => {
    let list = assetCategoryList;

    const keyword = (searchApplied.keyword || '').trim().toLowerCase();
    if (keyword) {
      list = list.filter(
        (x) =>
          (x.categoryCode || '').toLowerCase().includes(keyword) ||
          (x.categoryName || '').toLowerCase().includes(keyword),
      );
    }
    return list;
  }, [assetCategoryList, searchApplied]);

  const editor = useAssetCategoryEditor({
    fetchList: fetchAssetCategoryList,
    create,
    update,
    remove,
  });

  return {
    dataListView,
    isLoading,
    error,
    searchData: searchDraft,
    onSearchChange,
    onSelect,
    editor,
  };
};
