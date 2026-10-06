import { useCallback, useRef, useState } from 'react';
import { fetchAvailableLoanAssets, createLoanBySelf } from '@/entities/loan';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/**
 * S-420 대여 가능 자산 - 목록에서 찾은 뒤 QR 스캔으로 실제 대여를 시작한다("목록에서 바로 대여
 * 버튼을 주지 않는 이유는 실물 앞에 있음을 QR로 증명하게 하는 것" - 설계문서 §3).
 */
export const useAvailableAssets = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const [assets, setAssets] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [mode, setMode] = useState('list');
  const [lastFeedback, setLastFeedback] = useState(null);
  const [isBorrowing, setIsBorrowing] = useState(false);
  const borrowingRef = useRef(false);

  const load = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return;

    setIsLoading(true);
    try {
      const data = await fetchAvailableLoanAssets(currentConnector);
      if (data?.result === 'SUCCESS') {
        setAssets(data.data || []);
      } else {
        winiMsg.showSnackbar(data?.message || '대여 가능 자산 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('대여 가능 자산 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(load, Boolean(connector));

  const startScan = useCallback(() => {
    setLastFeedback(null);
    setMode('scan');
  }, []);

  const backToList = useCallback(() => {
    setLastFeedback(null);
    setMode('list');
    load();
  }, [load]);

  /**
   * 같은 QR을 카메라 앞에 계속 대고 있으면 스캐너 쿨다운(2.5s)이 지난 뒤 다시 호출될 수 있다 -
   * borrowingRef로 이전 요청이 끝나기 전 재호출을 막고, LoanScanView에는 isBorrowing을 paused로
   * 넘겨 처리 중에는 스캐너 자체를 멈춘다(이중 방어).
   */
  const onScanDecoded = useCallback(async (tangibleAssetId) => {
    if (borrowingRef.current) return;
    borrowingRef.current = true;
    setIsBorrowing(true);
    try {
      const data = await createLoanBySelf(connectorRef.current, { tangibleAssetId });
      if (data?.result === 'SUCCESS') {
        const asset = assets.find((a) => a.tangibleAssetId === tangibleAssetId);
        setLastFeedback({ ok: true, message: `${asset?.assetName || '자산'} 대여되었습니다.` });
      } else {
        setLastFeedback({ ok: false, message: data?.message || '대여 처리 중 오류가 발생했습니다.' });
      }
    } catch {
      setLastFeedback({ ok: false, message: '대여 처리 중 오류가 발생했습니다.' });
    } finally {
      borrowingRef.current = false;
      setIsBorrowing(false);
    }
  }, [assets]);

  return { assets, isLoading, mode, lastFeedback, isBorrowing, startScan, backToList, onScanDecoded, refresh: load };
};
