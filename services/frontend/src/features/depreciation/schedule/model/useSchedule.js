import { useCallback, useRef, useState } from 'react';
import { getDepreciationSchedule } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore } from '@/shared/model';

/**
 * 선택된 자산 1건의 해당 회계연도 4개 분기 상각 스케줄 조회 (S-231)
 */
export const useDepreciationSchedule = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');

  const [open, setOpen] = useState(false);
  const [asset, setAsset] = useState(null);
  const [schedule, setSchedule] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  const requestIdRef = useRef(0);

  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const openSchedule = useCallback(async (row, fiscalYear) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !row?.tangibleAssetId) return;

    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current)
      return;

    const requestId = ++requestIdRef.current;
    setAsset(row);
    setSchedule([]);
    setOpen(true);
    setIsLoading(true);
    try {
      const data = await getDepreciationSchedule(
        currentConnector,
        row.tangibleAssetId,
        fiscalYear,
      );
      if (requestId !== requestIdRef.current) return;

      if (data?.result === 'SUCCESS') {
        setSchedule(Array.isArray(data?.data) ? data.data : []);
      } else {
        winiMsg.showSnackbar(
          data?.message || '자산별 상각 스케줄 조회에 실패했습니다.',
        );
      }
    } catch {
      if (requestId === requestIdRef.current) {
        winiMsg.showSnackbar('자산별 상각 스케줄 조회 중 오류가 발생했습니다.');
      }
    } finally {
      if (requestId === requestIdRef.current) setIsLoading(false);
    }
  }, []);

  const closeSchedule = useCallback(() => {
    requestIdRef.current += 1;
    setOpen(false);
    setAsset(null);
    setSchedule([]);
    setIsLoading(false);
  }, []);

  return {
    open,
    asset,
    schedule,
    isLoading,
    openSchedule,
    closeSchedule,
  };
};
