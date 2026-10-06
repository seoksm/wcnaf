import { useCallback, useState } from 'react';
import { useVendorList } from '@/features/vendor/list';
import { useVendorActions, useVendorEditor } from '@/features/vendor/manage';

/** S-540 공급사 관리 */
export const useVendorManagementPage = () => {
  const { list, isLoading, fetchList } = useVendorList();
  const { create, update, remove } = useVendorActions();

  const [searchData, setSearchData] = useState({ keyword: '' });

  const onSearchChange = useCallback((e) => {
    const { name, value } = e.target;
    setSearchData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const onSearch = useCallback(() => {
    fetchList({ keyword: searchData.keyword || undefined });
  }, [fetchList, searchData.keyword]);

  const editor = useVendorEditor({ fetchList: onSearch, create, update, remove });

  return {
    list,
    isLoading,
    searchData,
    onSearchChange,
    onSearch,
    editor,
  };
};
