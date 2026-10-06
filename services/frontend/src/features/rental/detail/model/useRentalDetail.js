import { useCallback, useRef, useState } from 'react';
import { fetchPaymentSchedules, addPaymentSchedule, confirmPaymentSchedule, cancelRental } from '@/entities/rental';
import { winiCom, winiDate } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_NEW_SCHEDULE = { accrualMonth: '', dueDate: '', expectedAmount: '' };

/** S-512 렌탈·구독 상세 · 결제 스케줄 */
export const useRentalDetail = ({ onChanged }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [selectedRow, setSelectedRow] = useState(null);
  const [schedules, setSchedules] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isActing, setIsActing] = useState(false);
  const actingRef = useRef(false);
  const [newSchedule, setNewSchedule] = useState(EMPTY_NEW_SCHEDULE);
  const [actualAmountDraft, setActualAmountDraft] = useState({});

  const loadSchedules = useCallback(async (rentalAssetId) => {
    setIsLoading(true);
    try {
      const data = await fetchPaymentSchedules(connectorRef.current, rentalAssetId);
      if (data?.result === 'SUCCESS') setSchedules(data.data || []);
    } catch {
      winiMsg.showSnackbar('결제 스케줄 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const openDetail = useCallback((row) => {
    setSelectedRow(row);
    setNewSchedule(EMPTY_NEW_SCHEDULE);
    setActualAmountDraft({});
    loadSchedules(row.rentalAssetId);
  }, [loadSchedules]);

  const closeDetail = useCallback(() => setSelectedRow(null), []);

  const handleNewScheduleChange = useCallback((name, value) => {
    setNewSchedule((prev) => ({ ...prev, [name]: value }));
  }, []);

  /** WiniDatePicker 전용 - 다른 날짜 입력 화면(RentalDialog 등)과 동일한 표준 컨트롤로 맞춘다 */
  const handleNewScheduleDateChange = useCallback((name) => (event) => {
    const raw = event?.value ?? event?.target?.value;
    setNewSchedule((prev) => ({ ...prev, [name]: raw ? winiDate.dateFormat(raw, 'YYYY-MM-DD') : '' }));
  }, []);

  const addSchedule = useCallback(async () => {
    if (actingRef.current) return;
    if (!newSchedule.accrualMonth) {
      winiMsg.showAlert('귀속월을 입력해주세요.');
      return;
    }
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await addPaymentSchedule(connectorRef.current, selectedRow.rentalAssetId, {
        accrualMonth: newSchedule.accrualMonth,
        dueDate: newSchedule.dueDate || undefined,
        expectedAmount: newSchedule.expectedAmount ? Number(newSchedule.expectedAmount) : undefined,
      });
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('추가되었습니다.');
        setNewSchedule(EMPTY_NEW_SCHEDULE);
        await loadSchedules(selectedRow.rentalAssetId);
      } else {
        winiMsg.showSnackbar(data?.message || '추가 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('추가 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedRow, newSchedule, loadSchedules]);

  const setActualAmountForRow = useCallback((paymentScheduleId, value) => {
    setActualAmountDraft((prev) => ({ ...prev, [paymentScheduleId]: value }));
  }, []);

  const confirmSchedule = useCallback(async (paymentScheduleId) => {
    if (actingRef.current) return;
    const value = actualAmountDraft[paymentScheduleId];
    if (!value) {
      winiMsg.showAlert('실제금액을 입력해주세요.');
      return;
    }
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await confirmPaymentSchedule(connectorRef.current, paymentScheduleId, Number(value));
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('확정되었습니다.');
        await loadSchedules(selectedRow.rentalAssetId);
      } else {
        winiMsg.showSnackbar(data?.message || '확정 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('확정 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedRow, actualAmountDraft, loadSchedules]);

  const cancel = useCallback(async () => {
    if (actingRef.current) return;
    const answer = await winiMsg.showConfirm('해지하시겠습니까? 지출 이력은 그대로 보존됩니다.');
    if (answer !== 'Y') return;

    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await cancelRental(connectorRef.current, selectedRow.rentalAssetId);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('해지되었습니다.');
        setSelectedRow(null);
        await onChanged?.();
      } else {
        winiMsg.showSnackbar(data?.message || '해지 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('해지 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedRow, onChanged]);

  return {
    selectedRow, schedules, isLoading, isActing, newSchedule, actualAmountDraft,
    openDetail, closeDetail, handleNewScheduleChange, handleNewScheduleDateChange,
    addSchedule, setActualAmountForRow, confirmSchedule, cancel,
  };
};
