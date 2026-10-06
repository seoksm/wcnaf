import { useCallback, useState } from 'react';
import { useRentalList } from '@/features/rental/list';
import { useRentalDialog } from '@/features/rental/manage';
import { useRentalDetail } from '@/features/rental/detail';

/** S-510 렌탈·구독 목록 + S-511 등록/수정 + S-512 상세·결제스케줄 */
export const useRentalManagementPage = () => {
  const { list, pageInfo, isLoading, fetchList } = useRentalList();
  const [searchData, setSearchData] = useState({ keyword: '' });

  const onSearchChange = useCallback((e) => {
    const { name, value } = e.target;
    setSearchData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const runSearch = useCallback((page = 0) => {
    fetchList({ keyword: searchData.keyword || undefined, page, size: pageInfo.pageSize });
  }, [fetchList, searchData.keyword, pageInfo.pageSize]);

  const onSearch = useCallback(() => runSearch(0), [runSearch]);
  const onPageChange = useCallback((page) => runSearch(page), [runSearch]);

  const dialog = useRentalDialog({ onSuccess: onSearch });
  const detail = useRentalDetail({ onChanged: onSearch });
  // 훅이 매 렌더마다 새 객체를 반환하므로 detail/dialog 전체를 의존성에 넣으면 아래 콜백도
  // 매번 재생성된다. 실제로 쓰는 핸들러만 구조 분해해 그 함수의 메모이제이션을 그대로 물려받는다.
  const { openDetail, closeDetail } = detail;
  const { openEdit } = dialog;

  const onRowSelect = useCallback((row) => openDetail(row), [openDetail]);
  const onEditFromDetail = useCallback((row) => {
    closeDetail();
    openEdit(row);
  }, [closeDetail, openEdit]);

  return { list, pageInfo, isLoading, searchData, onSearchChange, onSearch, onPageChange, onRowSelect, dialog, detail, onEditFromDetail };
};
