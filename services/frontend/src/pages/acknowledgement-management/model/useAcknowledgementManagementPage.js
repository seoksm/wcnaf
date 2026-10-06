import { useCallback, useRef, useState } from 'react';
import { useAcknowledgementList } from '@/features/acknowledgement/list';
import { useRequestAcknowledgementDialog } from '@/features/acknowledgement/request';
import { useAcknowledgementDetail } from '@/features/acknowledgement/detail';
import { fetchCommonUsers } from '@/entities/tangibleAsset';
import { winiCom } from '@/shared/lib';
import { useInitialFetch } from '@/shared/model';

/** S-431 확인서 현황 + S-430 요청 + S-432 상세·담당자승인 */
export const useAcknowledgementManagementPage = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const { list, pageInfo, isLoading, fetchList } = useAcknowledgementList();
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
    fetchList({ status: searchData.status || undefined, page: 0, size: pageInfo.pageSize });
  }, [fetchList, searchData.status, pageInfo.pageSize]);

  const onPageChange = useCallback((page) => {
    fetchList({ status: searchData.status || undefined, page, size: pageInfo.pageSize });
  }, [fetchList, searchData.status, pageInfo.pageSize]);

  const requestDialog = useRequestAcknowledgementDialog({ onSuccess: onSearch });
  const detail = useAcknowledgementDetail({ onChanged: onSearch });

  return {
    list, pageInfo, isLoading, searchData, memberNameById,
    onSearchChange, onSearch, onPageChange,
    requestDialog, detail,
  };
};
