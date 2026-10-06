import { useCallback, useRef, useState } from 'react';
import { fetchIntangibleAssetActionLog, renewIntangibleAsset, deleteIntangibleAsset } from '@/entities/intangibleAsset';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_RENEW_FORM = { newExpiryDate: '', note: '' };

/** S-502 무형자산 상세 · 갱신 이력 */
export const useIntangibleAssetDetail = ({ onChanged }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [selectedRow, setSelectedRow] = useState(null);
  const [actionLog, setActionLog] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isActing, setIsActing] = useState(false);
  const [renewForm, setRenewForm] = useState(EMPTY_RENEW_FORM);

  const loadActionLog = useCallback(async (intangibleAssetId) => {
    setIsLoading(true);
    try {
      const data = await fetchIntangibleAssetActionLog(connectorRef.current, intangibleAssetId);
      if (data?.result === 'SUCCESS') setActionLog(data.data || []);
    } catch {
      winiMsg.showSnackbar('갱신 이력 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const openDetail = useCallback((row) => {
    setSelectedRow(row);
    setRenewForm(EMPTY_RENEW_FORM);
    loadActionLog(row.intangibleAssetId);
  }, [loadActionLog]);

  const closeDetail = useCallback(() => setSelectedRow(null), []);

  const handleRenewFormChange = useCallback((name, value) => {
    setRenewForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  const submitRenew = useCallback(async () => {
    if (!renewForm.newExpiryDate) {
      winiMsg.showAlert('새 만료일을 입력해주세요.');
      return;
    }
    setIsActing(true);
    try {
      const data = await renewIntangibleAsset(connectorRef.current, selectedRow.intangibleAssetId, renewForm.newExpiryDate, renewForm.note || undefined);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('갱신되었습니다.');
        await loadActionLog(selectedRow.intangibleAssetId);
        await onChanged?.();
        setSelectedRow((prev) => ({ ...prev, expiryDate: renewForm.newExpiryDate }));
        setRenewForm(EMPTY_RENEW_FORM);
      } else {
        winiMsg.showSnackbar(data?.message || '갱신 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('갱신 중 오류가 발생했습니다.');
    } finally {
      setIsActing(false);
    }
  }, [selectedRow, renewForm, loadActionLog, onChanged]);

  const removeAsset = useCallback(async () => {
    const answer = await winiMsg.showConfirm('삭제(사용 해제)하시겠습니까?');
    if (answer !== 'Y') return;

    setIsActing(true);
    try {
      const data = await deleteIntangibleAsset(connectorRef.current, selectedRow.intangibleAssetId);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('삭제되었습니다.');
        setSelectedRow(null);
        await onChanged?.();
      } else {
        winiMsg.showSnackbar(data?.message || '삭제 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('삭제 중 오류가 발생했습니다.');
    } finally {
      setIsActing(false);
    }
  }, [selectedRow, onChanged]);

  return { selectedRow, actionLog, isLoading, isActing, renewForm, openDetail, closeDetail, handleRenewFormChange, submitRenew, removeAsset };
};
