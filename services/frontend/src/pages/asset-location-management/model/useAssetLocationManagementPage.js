import { useCallback, useMemo, useState } from 'react';
import { useAssetLocationList } from '@/features/assetLocation/list';
import {
  useAssetLocationActions,
  useAssetLocationEditor,
} from '@/features/assetLocation/manage';

export const useAssetLocationManagementPage = () => {
  const { assetLocationList, isLoading, error, fetchAssetLocationList } =
    useAssetLocationList();
  const { create, update, remove } = useAssetLocationActions();

  const [searchDraft, setSearchDraft] = useState({ keyword: '' });
  const [searchApplied, setSearchApplied] = useState({ keyword: '' });

  const onSearchChange = useCallback((e) => {
    const { name, value } = e.target;
    setSearchDraft((prev) => ({ ...prev, [name]: value }));
  }, []);

  const onSelect = useCallback(async () => {
    setSearchApplied({ keyword: (searchDraft.keyword || '').trim() });
    await fetchAssetLocationList();
  }, [searchDraft, fetchAssetLocationList]);

  // 최초 1회 로드는 useAssetLocationList 내부(useInitialFetch)에서 처리한다

  const dataListView = useMemo(() => {
    let list = assetLocationList;

    const keyword = (searchApplied.keyword || '').trim().toLowerCase();
    if (keyword) {
      list = list.filter((x) =>
        (x.locationName || '').toLowerCase().includes(keyword),
      );
    }
    return list;
  }, [assetLocationList, searchApplied]);

  const editor = useAssetLocationEditor({
    fetchList: fetchAssetLocationList,
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
