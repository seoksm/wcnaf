import { useCallback, useRef, useState } from 'react';
import { usePendingApprovalList } from '@/features/loan/pendingApproval';
import { fetchCommonUsers } from '@/entities/tangibleAsset';
import { winiCom } from '@/shared/lib';
import { useInitialFetch } from '@/shared/model';

/** S-411 대여 승인대기 */
export const useLoanPendingApprovalPage = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const list = usePendingApprovalList();
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

  return { ...list, memberNameById };
};
