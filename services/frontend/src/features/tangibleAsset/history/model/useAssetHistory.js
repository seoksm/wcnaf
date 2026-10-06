import { useCallback, useEffect, useRef, useState } from 'react';
import { getAssetHistory } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore } from '@/shared/model';

/**
 * 선택된 유형자산 1건의 변경 이력 조회 (S-220)
 */
export const useAssetHistory = (tangibleAssetId) => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const formMenuIdRef = useRef(formMenuId);
  formMenuIdRef.current = formMenuId;

  const [history, setHistory] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchHistory = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !tangibleAssetId) {
      setHistory([]);
      return;
    }

    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return;

    setIsLoading(true);
    try {
      const data = await getAssetHistory(currentConnector, tangibleAssetId);
      if (data?.result === 'SUCCESS') {
        setHistory(Array.isArray(data?.data) ? data.data : []);
      }
    } catch {
      winiMsg.showSnackbar('변경 이력 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, [tangibleAssetId]);

  useEffect(() => {
    fetchHistory();
  }, [fetchHistory]);

  return {
    history,
    isLoading,
  };
};
