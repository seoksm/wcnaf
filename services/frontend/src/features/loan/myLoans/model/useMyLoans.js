import { useCallback, useRef, useState } from 'react';
import { fetchMyLoans, returnLoanBySelf, extendLoanBySelf } from '@/entities/loan';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/**
 * S-421 내 대여 자산 - 반납은 QR 재스캔(L3)으로, 스캔된 자산이 본인의 대여중(ACTIVE) 건과
 * 일치할 때만 처리한다(소유권 검사, S-311의 자가서비스 패턴과 동일).
 */
export const useMyLoans = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const [loans, setLoans] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isActing, setIsActing] = useState(false);
  const actingRef = useRef(false);
  const [mode, setMode] = useState('list');
  const [lastFeedback, setLastFeedback] = useState(null);
  const [pendingReturn, setPendingReturn] = useState(null);

  const load = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return;

    setIsLoading(true);
    try {
      const data = await fetchMyLoans(currentConnector);
      if (data?.result === 'SUCCESS') {
        setLoans(data.data || []);
      } else {
        winiMsg.showSnackbar(data?.message || '내 대여 자산 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('내 대여 자산 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(load, Boolean(connector));

  const activeLoans = loans.filter((l) => l.status === 'ACTIVE');

  const startReturnScan = useCallback(() => {
    if (activeLoans.length === 0) {
      winiMsg.showAlert('반납할 대여 중인 자산이 없습니다.');
      return;
    }
    setLastFeedback(null);
    setMode('scan');
  }, [activeLoans.length]);

  const backToList = useCallback(() => {
    setLastFeedback(null);
    setMode('list');
    load();
  }, [load]);

  /** ScanView가 QR을 디코딩할 때마다 호출 - 본인의 대여중 건과 매칭되면 상태확인 다이얼로그를 연다.
   * pendingReturn이 세팅된 동안은 LoanScanView에 paused로 전달돼 스캐너 자체가 멈추므로,
   * 확인 다이얼로그가 떠 있는 사이 다른 QR이 끼어들어 대상이 바뀌는 일은 없다. */
  const onScanDecoded = useCallback((tangibleAssetId) => {
    const target = activeLoans.find((l) => l.tangibleAssetId === tangibleAssetId);
    if (!target) {
      setLastFeedback({ ok: false, message: '본인이 대여한 자산이 아니거나 이미 반납된 자산입니다.' });
      return;
    }
    setPendingReturn(target);
  }, [activeLoans]);

  const cancelReturn = useCallback(() => setPendingReturn(null), []);

  const confirmReturn = useCallback(async (abnormal) => {
    if (actingRef.current || !pendingReturn) return;
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await returnLoanBySelf(connectorRef.current, pendingReturn.loanId, abnormal);
      if (data?.result === 'SUCCESS') {
        setLastFeedback({ ok: true, message: `${pendingReturn.assetName}(${pendingReturn.assetCode}) 반납되었습니다.` });
        setLoans((prev) => prev.map((l) => (l.loanId === pendingReturn.loanId ? { ...l, status: 'RETURNED' } : l)));
      } else {
        setLastFeedback({ ok: false, message: data?.message || '반납 처리 중 오류가 발생했습니다.' });
      }
    } catch {
      setLastFeedback({ ok: false, message: '반납 처리 중 오류가 발생했습니다.' });
    } finally {
      actingRef.current = false;
      setIsActing(false);
      setPendingReturn(null);
    }
  }, [pendingReturn]);

  const extend = useCallback(async (loanId) => {
    if (actingRef.current) return;
    const answer = await winiMsg.showConfirm('대여 기한을 연장하시겠습니까?');
    if (answer !== 'Y') return;

    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await extendLoanBySelf(connectorRef.current, loanId);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('연장되었습니다.');
        await load();
      } else {
        winiMsg.showSnackbar(data?.message || '연장 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('연장 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [load]);

  return {
    loans, isLoading, isActing, mode, lastFeedback, pendingReturn,
    startReturnScan, backToList, onScanDecoded, cancelReturn, confirmReturn, extend,
  };
};
