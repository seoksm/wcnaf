import { useCallback, useRef, useState } from 'react';
import { fetchPendingLoans, approveLoan, rejectLoan } from '@/entities/loan';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/** S-411 대여 승인대기 - 목록 조회 + 승인/반려 처리 */
export const usePendingApprovalList = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const [pendingList, setPendingList] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [isActing, setIsActing] = useState(false);
  const actingRef = useRef(false);
  const [rejectDialogOpen, setRejectDialogOpen] = useState(false);
  const [rejectReason, setRejectReason] = useState('');

  const load = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return;

    setIsLoading(true);
    try {
      const data = await fetchPendingLoans(currentConnector);
      if (data?.result === 'SUCCESS') {
        setPendingList(data.data || []);
      } else {
        winiMsg.showSnackbar(data?.message || '승인대기 목록 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('승인대기 목록 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(load, Boolean(connector));

  const onRowSelect = useCallback((row) => setSelectedRow(row), []);

  const approve = useCallback(async () => {
    if (actingRef.current || !selectedRow) return;
    const answer = await winiMsg.showConfirm(`${selectedRow.assetName}(${selectedRow.assetCode}) 대여를 승인하시겠습니까?`);
    if (answer !== 'Y') return;

    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await approveLoan(connectorRef.current, selectedRow.loanId);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('승인되었습니다.');
        setSelectedRow(null);
        await load();
      } else {
        winiMsg.showSnackbar(data?.message || '승인 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('승인 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedRow, load]);

  const openRejectDialog = useCallback(() => {
    if (!selectedRow) {
      winiMsg.showAlert('반려할 대여를 선택해주세요.');
      return;
    }
    setRejectReason('');
    setRejectDialogOpen(true);
  }, [selectedRow]);

  const closeRejectDialog = useCallback(() => setRejectDialogOpen(false), []);

  const submitReject = useCallback(async () => {
    if (actingRef.current) return;
    if (!rejectReason.trim()) {
      winiMsg.showAlert('반려 사유를 입력해주세요.');
      return;
    }
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await rejectLoan(connectorRef.current, selectedRow.loanId, rejectReason.trim());
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('반려되었습니다.');
        setRejectDialogOpen(false);
        setSelectedRow(null);
        await load();
      } else {
        winiMsg.showSnackbar(data?.message || '반려 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('반려 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedRow, rejectReason, load]);

  return {
    pendingList, isLoading, selectedRow, isActing,
    onRowSelect, approve,
    rejectDialogOpen, rejectReason, setRejectReason, openRejectDialog, closeRejectDialog, submitReject,
  };
};
