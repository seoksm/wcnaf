import { useCallback, useRef, useState } from 'react';
import { disuseTangibleAsset } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

/**
 * 유형자산 불용 처리 모달 (S-241) - 선택한 자산 1건을 불용 상태로 전환한다.
 * 배정되어 있으면 서버가 자동으로 해제하므로(A1), 여기서는 사유만 입력받는다.
 */
export const useDisuse = ({ fetchList, onDone }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [open, setOpen] = useState(false);
  const [targetAsset, setTargetAsset] = useState(null);
  const [reason, setReason] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const submittingRef = useRef(false);

  const openDialog = useCallback((asset) => {
    if (!asset?.tangibleAssetId) return;
    setTargetAsset(asset);
    setReason('');
    setOpen(true);
  }, []);

  const closeDialog = useCallback(() => setOpen(false), []);

  const onReasonChange = useCallback((e) => setReason(e.target.value), []);

  const submit = useCallback(async () => {
    if (submittingRef.current || !connectorRef.current || !targetAsset?.tangibleAssetId) return;

    submittingRef.current = true;
    const answer = await winiMsg.showConfirm('선택한 자산을 불용 처리하시겠습니까?\n배정되어 있다면 자동으로 해제됩니다.');
    if (answer !== 'Y') {
      submittingRef.current = false;
      return;
    }

    setIsSubmitting(true);
    try {
      const data = await disuseTangibleAsset(connectorRef.current, targetAsset.tangibleAssetId, reason.trim());
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('불용 처리 되었습니다.');
        closeDialog();
        await fetchList?.();
        await onDone?.();
      } else {
        winiMsg.showSnackbar(data?.message || '불용 처리 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('불용 처리 중 오류가 발생했습니다.');
    } finally {
      submittingRef.current = false;
      setIsSubmitting(false);
    }
  }, [targetAsset, reason, fetchList, onDone, closeDialog]);

  return {
    open,
    targetAsset,
    reason,
    isSubmitting,
    openDialog,
    closeDialog,
    onReasonChange,
    submit,
  };
};
