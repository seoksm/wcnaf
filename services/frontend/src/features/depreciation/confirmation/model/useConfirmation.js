import { useCallback, useRef, useState } from 'react';
import {
  getDepreciationConfirmationLog,
  postConfirmDepreciation,
  postReleaseDepreciation,
} from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, useInitialFetch } from '@/shared/model';

/**
 * 결산 확정/해제 실행 + 확정·해제 이력 조회 (S-232)
 */
export const useDepreciationConfirmation = ({
  fiscalYear,
  quarter,
  onChanged,
}) => {
  const { connector } = winiCom.getFormInfo('Y');

  const [log, setLog] = useState([]);
  const [logDialogOpen, setLogDialogOpen] = useState(false);
  const [releaseReason, setReleaseReason] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLogLoading, setIsLogLoading] = useState(false);
  const connectorRef = useRef(connector);
  const onChangedRef = useRef(onChanged);
  const actionInFlightRef = useRef(false);
  const logLoadingRef = useRef(false);

  connectorRef.current = connector;
  onChangedRef.current = onChanged;

  const fetchLog = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || logLoadingRef.current) return null;

    logLoadingRef.current = true;
    setIsLogLoading(true);
    try {
      const data = await getDepreciationConfirmationLog(currentConnector);
      if (data?.result === 'SUCCESS') {
        setLog(Array.isArray(data?.data) ? data.data : []);
      } else {
        winiMsg.showSnackbar(
          data?.message || '확정·해제 이력 조회에 실패했습니다.',
        );
      }
      return data;
    } catch {
      winiMsg.showSnackbar('확정·해제 이력 조회 중 오류가 발생했습니다.');
      return null;
    } finally {
      logLoadingRef.current = false;
      setIsLogLoading(false);
    }
  }, []);

  useInitialFetch(fetchLog, Boolean(connector));

  const openLogDialog = useCallback(async () => {
    setLogDialogOpen(true);
    await fetchLog();
  }, [fetchLog]);

  const closeLogDialog = useCallback(() => setLogDialogOpen(false), []);

  const handleConfirm = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (
      !currentConnector ||
      !fiscalYear ||
      !quarter ||
      actionInFlightRef.current
    )
      return;

    actionInFlightRef.current = true;
    setIsSubmitting(true);

    try {
      const answer = await winiMsg.showConfirm(
        `${fiscalYear}년 ${quarter} 감가상각 결산을 확정하시겠습니까?\n확정 후에는 해당 기간이 스냅샷으로 고정됩니다.`,
      );
      if (answer !== 'Y') return;

      const data = await postConfirmDepreciation(currentConnector, {
        fiscalYear,
        quarter,
      });
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('확정 되었습니다.');
        await Promise.all([onChangedRef.current?.(), fetchLog()]);
      } else {
        winiMsg.showSnackbar(data?.message || '확정 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('확정 중 오류가 발생했습니다.');
    } finally {
      actionInFlightRef.current = false;
      setIsSubmitting(false);
    }
  }, [fiscalYear, quarter, fetchLog]);

  const handleRelease = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (
      !currentConnector ||
      !fiscalYear ||
      !quarter ||
      actionInFlightRef.current
    )
      return;

    actionInFlightRef.current = true;
    setIsSubmitting(true);

    try {
      const answer = await winiMsg.showConfirm(
        `${fiscalYear}년 ${quarter} 감가상각 결산 확정을 해제하시겠습니까?\n해제 후에는 다시 실시간 산출로 표시됩니다.`,
      );
      if (answer !== 'Y') return;

      const data = await postReleaseDepreciation(currentConnector, {
        fiscalYear,
        quarter,
        reason: releaseReason || null,
      });
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('해제 되었습니다.');
        setReleaseReason('');
        await Promise.all([onChangedRef.current?.(), fetchLog()]);
      } else {
        winiMsg.showSnackbar(data?.message || '해제 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('해제 중 오류가 발생했습니다.');
    } finally {
      actionInFlightRef.current = false;
      setIsSubmitting(false);
    }
  }, [fiscalYear, quarter, releaseReason, fetchLog]);

  return {
    log,
    logDialogOpen,
    isSubmitting,
    isLogLoading,
    releaseReason,
    setReleaseReason,
    openLogDialog,
    closeLogDialog,
    handleConfirm,
    handleRelease,
  };
};
