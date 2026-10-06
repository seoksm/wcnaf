import { useCallback, useState } from 'react';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTicketList } from '@/features/ticket/list';
import { useTicketKanban } from '@/features/ticket/kanban';
import { useTicketDialog } from '@/features/ticket/manage';
import { useTicketDetail } from '@/features/ticket/detail';

/** S-600 칸반 보드 · S-601 목록(모바일 대체) - <768에서 칸반→상태탭+리스트로 전환(C4) */
export const useTicketManagementPage = () => {
  const isMobile = useMediaQuery('(max-width:767px)');

  const list = useTicketList();
  const kanban = useTicketKanban();
  // list/kanban 훅이 매 렌더마다 새 객체를 반환하므로, 실제로 쓰는 함수·값만 구조 분해해
  // 아래 콜백들의 메모이제이션이 무의미해지지 않게 한다.
  const { fetchList, pageInfo: listPageInfo } = list;
  const { reload: kanbanReload } = kanban;

  const [searchData, setSearchData] = useState({ keyword: '', status: '' });

  const onSearchChange = useCallback((e) => {
    const { name, value } = e.target;
    setSearchData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const runSearch = useCallback((page = 0) => {
    fetchList({ keyword: searchData.keyword || undefined, status: searchData.status || undefined, page, size: listPageInfo.pageSize });
  }, [fetchList, searchData, listPageInfo.pageSize]);

  const onSearch = useCallback(() => runSearch(0), [runSearch]);
  const onPageChange = useCallback((page) => runSearch(page), [runSearch]);

  const reload = useCallback(async () => {
    if (isMobile) {
      await runSearch(listPageInfo.currentPage);
    } else {
      await kanbanReload();
    }
  }, [isMobile, runSearch, listPageInfo.currentPage, kanbanReload]);

  const dialog = useTicketDialog({ onSuccess: reload });
  const detail = useTicketDetail({ onChanged: reload });

  return {
    isMobile,
    list, kanban,
    searchData, onSearchChange, onSearch, onPageChange,
    dialog, detail,
    onCardClick: detail.openDetail,
  };
};
