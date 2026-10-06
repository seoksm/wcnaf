import { useCallback, useRef, useState } from 'react';
import { createRental, updateRental } from '@/entities/rental';
import { winiCom, winiDate } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_FORM = { rentalAssetId: '', name: '', billingMode: 'FIXED', startDate: '', endDate: '', memo: '' };

/** S-511 렌탈·구독 등록/수정 - P-6 모달로 구현(S-510 목록 안에서 처리) */
export const useRentalDialog = ({ onSuccess }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const submittingRef = useRef(false);

  const openCreate = useCallback(() => {
    setForm(EMPTY_FORM);
    setOpen(true);
  }, []);

  const openEdit = useCallback((row) => {
    setForm({
      rentalAssetId: row.rentalAssetId,
      name: row.name || '',
      billingMode: row.billingMode || 'FIXED',
      startDate: row.startDate || '',
      endDate: row.endDate || '',
      memo: row.memo || '',
    });
    setOpen(true);
  }, []);

  const closeDialog = useCallback(() => setOpen(false), []);

  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleDateChange = useCallback((name) => (event) => {
    const raw = event?.value ?? event?.target?.value;
    setForm((prev) => ({ ...prev, [name]: raw ? winiDate.dateFormat(raw, 'YYYY-MM-DD') : '' }));
  }, []);

  const submit = useCallback(async () => {
    if (submittingRef.current) return;
    if (!form.name.trim()) {
      winiMsg.showAlert('렌탈·구독명은 필수입니다.');
      return;
    }
    if (!form.startDate) {
      winiMsg.showAlert('시작일은 필수입니다.');
      return;
    }
    const body = {
      name: form.name,
      billingMode: form.billingMode,
      startDate: form.startDate,
      endDate: form.endDate || undefined,
      memo: form.memo || undefined,
    };

    submittingRef.current = true;
    setIsSubmitting(true);
    try {
      const data = form.rentalAssetId
        ? await updateRental(connectorRef.current, form.rentalAssetId, body)
        : await createRental(connectorRef.current, body);

      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar(form.rentalAssetId ? '수정되었습니다.' : '등록되었습니다.');
        setOpen(false);
        await onSuccess?.();
      } else {
        winiMsg.showSnackbar(data?.message || '저장 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('저장 중 오류가 발생했습니다.');
    } finally {
      submittingRef.current = false;
      setIsSubmitting(false);
    }
  }, [form, onSuccess]);

  return { open, form, isSubmitting, openCreate, openEdit, closeDialog, handleChange, handleDateChange, submit };
};
