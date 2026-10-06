import { useCallback, useRef, useState } from 'react';
import { useLoanList } from '@/features/loan/list';
import { useAdminBorrowDialog } from '@/features/loan/adminBorrow';
import { fetchCommonUsers } from '@/entities/tangibleAsset';
import { winiCom } from '@/shared/lib';
import { useInitialFetch } from '@/shared/model';

/** S-410 대여 현황 + S-412 관리자 대행 등록 진입점 */
export const useLoanManagementPage = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const { loanList, pageInfo, isLoading, fetchLoans } = useLoanList();
  const [searchData, setSearchData] = useState({ status: '' });
  const [memberNameById, setMemberNameById] = useState({});

  const loadMemberNames = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    const data = await fetchCommonUsers(currentConnector);
    if (data?.result === 'SUCCESS') {
      const map = {};
      (data.data || []).forEach((user) => { map[user.id] = user.fullName || user.username; });
      setMemberNameById(map);
    }
  }, []);

  useInitialFetch(loadMemberNames, Boolean(connector));

  const onSearchChange = useCallback((e) => {
    const { name, value } = e.target;
    setSearchData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const onSearch = useCallback(() => {
    fetchLoans({ status: searchData.status || undefined, page: 0, size: pageInfo.pageSize });
  }, [fetchLoans, searchData.status, pageInfo.pageSize]);

  const onPageChange = useCallback((page) => {
    fetchLoans({ status: searchData.status || undefined, page, size: pageInfo.pageSize });
  }, [fetchLoans, searchData.status, pageInfo.pageSize]);

  const adminBorrow = useAdminBorrowDialog({ onSuccess: onSearch });

  return {
    loanList, pageInfo, isLoading, searchData, memberNameById,
    onSearchChange, onSearch, onPageChange,
    adminBorrow,
  };
};
