import { useCallback, useEffect, useRef, useState } from 'react';
import { getAssignmentHistory, releaseAssignment } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore } from '@/shared/model';

/**
 * 선택된 유형자산의 배정 이력 조회 · 회수 처리
 */
export const useAssignmentHistory = (tangibleAssetId, onReleased) => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const formMenuIdRef = useRef(formMenuId);
  formMenuIdRef.current = formMenuId;

  const [history, setHistory] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isReleasing, setIsReleasing] = useState(false);
  const releasingRef = useRef(false);

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
      const data = await getAssignmentHistory(currentConnector, tangibleAssetId);
      if (data?.result === 'SUCCESS') {
        setHistory(Array.isArray(data?.data) ? data.data : []);
      }
    } catch {
      winiMsg.showSnackbar('배정 이력 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, [tangibleAssetId]);

  useEffect(() => {
    fetchHistory();
  }, [fetchHistory]);

  const release = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !tangibleAssetId || releasingRef.current) return;

    releasingRef.current = true;
    const answer = await winiMsg.showConfirm('현재 배정을 회수하시겠습니까?');
    if (answer !== 'Y') {
      releasingRef.current = false;
      return;
    }

    setIsReleasing(true);
    try {
      const data = await releaseAssignment(currentConnector, tangibleAssetId);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('회수 되었습니다.');
        await fetchHistory();
        await onReleased?.();
      } else {
        winiMsg.showSnackbar(data?.message || '회수 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('회수 중 오류가 발생했습니다.');
    } finally {
      releasingRef.current = false;
      setIsReleasing(false);
    }
  }, [tangibleAssetId, fetchHistory, onReleased]);

  return {
    history,
    isLoading,
    isReleasing,
    release,
  };
};
