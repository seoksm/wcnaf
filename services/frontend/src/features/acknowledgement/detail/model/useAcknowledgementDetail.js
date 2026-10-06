import { useCallback, useRef, useState } from 'react';
import { fetchAcknowledgementDetail, managerApproveAcknowledgement, cancelAcknowledgement } from '@/entities/acknowledgement';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const DEFAULT_APPROVE_FORM = { returnCondition: 'NORMAL', nextLifeStatus: 'STORAGE', nextAssignType: 'UNASSIGNED' };

/** S-432 확인서 상세 · 담당자 승인 (관리자) */
export const useAcknowledgementDetail = ({ onChanged }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [selectedId, setSelectedId] = useState(null);
  const [detail, setDetail] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isActing, setIsActing] = useState(false);
  const actingRef = useRef(false);
  const [approveForm, setApproveForm] = useState(DEFAULT_APPROVE_FORM);
  const [cancelReason, setCancelReason] = useState('');

  const load = useCallback(async (id) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !id) return;
    setIsLoading(true);
    try {
      const data = await fetchAcknowledgementDetail(currentConnector, id);
      if (data?.result === 'SUCCESS') {
        setDetail(data.data);
      } else {
        winiMsg.showSnackbar(data?.message || '확인서 상세 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('확인서 상세 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const openDetail = useCallback((row) => {
    setSelectedId(row.acknowledgementId);
    setApproveForm(DEFAULT_APPROVE_FORM);
    setCancelReason('');
    load(row.acknowledgementId);
  }, [load]);

  const closeDetail = useCallback(() => setSelectedId(null), []);

  const handleApproveFormChange = useCallback((e) => {
    const { name, value } = e.target;
    setApproveForm((prev) => {
      if (name === 'returnCondition' && value === 'ABNORMAL') {
        return { ...prev, returnCondition: value, nextLifeStatus: 'REPAIR' };
      }
      return { ...prev, [name]: value };
    });
  }, []);

  const submitManagerApprove = useCallback(async () => {
    if (actingRef.current) return;
    const answer = await winiMsg.showConfirm('담당자 승인과 동시에 자산 상태가 확정됩니다.\n승인하시겠습니까?');
    if (answer !== 'Y') return;
    if (actingRef.current) return;

    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await managerApproveAcknowledgement(connectorRef.current, selectedId, approveForm);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('승인되었습니다.');
        await load(selectedId);
        await onChanged?.();
      } else {
        winiMsg.showSnackbar(data?.message || '승인 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('승인 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedId, approveForm, load, onChanged]);

  const submitCancel = useCallback(async () => {
    if (actingRef.current) return;
    if (!cancelReason.trim()) {
      winiMsg.showAlert('취소 사유를 입력해주세요.');
      return;
    }
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await cancelAcknowledgement(connectorRef.current, selectedId, cancelReason.trim());
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('요청이 취소되었습니다.');
        await load(selectedId);
        await onChanged?.();
      } else {
        winiMsg.showSnackbar(data?.message || '취소 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('취소 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedId, cancelReason, load, onChanged]);

  return {
    selectedId, detail, isLoading, isActing,
    approveForm, cancelReason, setCancelReason,
    openDetail, closeDetail, handleApproveFormChange, submitManagerApprove, submitCancel,
  };
};
