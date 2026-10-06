import { useCallback, useState } from 'react';
import { useSoftwareList } from '@/features/software/list';
import { useSoftwareActions, useSoftwareEditor } from '@/features/software/manage';

/** S-520 소프트웨어 마스터 관리 */
export const useSoftwareManagementPage = () => {
  const { list, isLoading, fetchList } = useSoftwareList();
  const { create, update, remove } = useSoftwareActions();

  const [searchData, setSearchData] = useState({ keyword: '' });

  const onSearchChange = useCallback((e) => {
    const { name, value } = e.target;
    setSearchData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const onSearch = useCallback(() => {
    fetchList({ keyword: searchData.keyword || undefined });
  }, [fetchList, searchData.keyword]);

  const editor = useSoftwareEditor({ fetchList: onSearch, create, update, remove });

  return {
    list,
    isLoading,
    searchData,
    onSearchChange,
    onSearch,
    editor,
  };
};
