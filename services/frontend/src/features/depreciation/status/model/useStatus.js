import { useCallback, useEffect, useRef, useState } from 'react';
import { getDepreciationStatus } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore } from '@/shared/model';

/**
 * 선택된 회계연도·누적분기의 감가상각 현황 조회 (S-230)
 */
export const useDepreciationStatus = (fiscalYear, quarter) => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const formMenuIdRef = useRef(formMenuId);
  formMenuIdRef.current = formMenuId;

  const [rows, setRows] = useState([]);
  const [summary, setSummary] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const fetchStatus = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !fiscalYear || !quarter) return null;

    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return null;

    setIsLoading(true);
    try {
      const data = await getDepreciationStatus(currentConnector, fiscalYear, quarter);
      if (data?.result === 'SUCCESS') {
        setRows(Array.isArray(data?.data?.rows) ? data.data.rows : []);
        setSummary(data?.data?.summary || null);
      } else {
        winiMsg.showSnackbar(
          data?.message || '감가상각 현황 조회에 실패했습니다.',
        );
      }
      return data;
    } catch {
      winiMsg.showSnackbar('감가상각 현황 조회 중 오류가 발생했습니다.');
      return null;
    } finally {
      setIsLoading(false);
    }
  }, [fiscalYear, quarter]);

  useEffect(() => {
    fetchStatus();
  }, [fetchStatus]);

  return {
    rows,
    summary,
    isLoading,
    refetch: fetchStatus,
  };
};
