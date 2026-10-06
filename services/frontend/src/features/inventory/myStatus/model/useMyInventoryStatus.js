import { useCallback, useRef, useState } from 'react';
import {
  fetchMyInventoryStatus,
  selfConfirmInventoryResult,
  selfConfirmInventoryResultWithoutScan,
  selfReportWrongHolder,
} from '../api/api';
import { usePhotoCapture } from '@/features/inventory/scan';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/**
 * S-310 내 전수조사 - 본인이 배정받은 대상 목록과, 그 대상에 대한 3가지 확인 경로(§1/§3):
 * ① QR 연속 스캔(S-311, ScanView가 호출) ② 라벨 없음/훼손 - 사진으로 확인(I3 예외, 승인 강제)
 * ③ "제 자산이 아닙니다"(타인보유 이상 보고). 관리자 메뉴 권한과 무관하게 소유권만으로 동작한다.
 */
export const useMyInventoryStatus = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const formMenuIdRef = useRef(formMenuId);
  formMenuIdRef.current = formMenuId;
  const { capture, isUploading } = usePhotoCapture();

  const [status, setStatus] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isActing, setIsActing] = useState(false);
  const [mode, setMode] = useState('list'); // 'list' | 'scan'
  const [lastFeedback, setLastFeedback] = useState(null);

  const load = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;

    // 메뉴 전환 중 이전 페이지가 잠깐 더 마운트된 채로 새 메뉴의 connector를 받아 자기 데이터를
    // 재요청해버리면 잘못된 메뉴 권한으로 호출돼 "권한없음"이 뜬다(useInitialFetch 주석 참고) -
    // 이 훅이 속한 메뉴가 지금 실제로 활성화된 메뉴가 맞는지 먼저 확인한다.
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return;

    setIsLoading(true);
    try {
      const data = await fetchMyInventoryStatus(currentConnector);
      if (data?.result === 'SUCCESS') {
        setStatus(data.data);
      } else {
        winiMsg.showSnackbar(data?.message || '내 전수조사 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('내 전수조사 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(load, Boolean(connector));

  const unconfirmedResults = (status?.results || []).filter((r) => r.status === 'UNCONFIRMED');

  const startScan = useCallback(() => {
    if (unconfirmedResults.length === 0) {
      winiMsg.showAlert('확인이 필요한 자산이 없습니다.');
      return;
    }
    setLastFeedback(null);
    setMode('scan');
  }, [unconfirmedResults.length]);

  const backToList = useCallback(() => {
    setLastFeedback(null);
    setMode('list');
  }, []);

  /** ScanView가 QR을 디코딩할 때마다 호출된다 - 본인 소유의 미확인 대상과 매칭되는지 확인 후 처리 */
  const confirmByScan = useCallback(async (tangibleAssetId) => {
    const target = unconfirmedResults.find((r) => r.tangibleAssetId === tangibleAssetId);
    if (!target) {
      setLastFeedback({ ok: false, message: '본인에게 배정된 대상이 아니거나 이미 확인된 자산입니다.' });
      return;
    }
    try {
      const data = await selfConfirmInventoryResult(connectorRef.current, target.inventoryTargetId);
      if (data?.result !== 'SUCCESS') {
        setLastFeedback({ ok: false, message: data?.message || '확인 처리 중 오류가 발생했습니다.' });
        return;
      }
      await load();
      setLastFeedback({ ok: true, message: `${target.assetName}(${target.assetCode}) 확인되었습니다.` });
    } catch {
      setLastFeedback({ ok: false, message: '확인 처리 중 오류가 발생했습니다.' });
    }
  }, [unconfirmedResults, load]);

  const confirmWithPhoto = useCallback(async (inventoryTargetId, file) => {
    if (isActing) return;
    const captured = await capture(file);
    if (!captured) return;

    setIsActing(true);
    try {
      const data = await selfConfirmInventoryResultWithoutScan(
        connectorRef.current, inventoryTargetId, captured.photoFileId, captured.capturedAt, captured.uploadedAt,
      );
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('사진으로 확인 처리되었습니다. 관리자 승인 후 최종 반영됩니다.');
        await load();
      } else {
        winiMsg.showSnackbar(data?.message || '확인 처리 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('확인 처리 중 오류가 발생했습니다.');
    } finally {
      setIsActing(false);
    }
  }, [isActing, capture, load]);

  const reportWrongHolder = useCallback(async (inventoryTargetId) => {
    const answer = await winiMsg.showConfirm('이 자산은 제 것이 아닙니다.\n이상 보고 처리하시겠습니까?');
    if (answer !== 'Y') return;

    setIsActing(true);
    try {
      const data = await selfReportWrongHolder(connectorRef.current, inventoryTargetId, '임직원 본인이 타인보유로 신고함');
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('이상 보고 되었습니다. 관리자가 배정을 확인할 예정입니다.');
        await load();
      } else {
        winiMsg.showSnackbar(data?.message || '이상 보고 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('이상 보고 중 오류가 발생했습니다.');
    } finally {
      setIsActing(false);
    }
  }, [load]);

  return {
    status,
    isLoading,
    isActing: isActing || isUploading,
    mode,
    unconfirmedResults,
    lastFeedback,
    startScan,
    backToList,
    confirmByScan,
    confirmWithPhoto,
    reportWrongHolder,
    refresh: load,
  };
};
