import { useCallback, useRef, useState } from 'react';
import { winiMsg } from '@/shared/model';

const EMPTY_DATA = {
  vendorId: '',
  name: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  memo: '',
};

/** S-540 공급사 등록/수정 폼 상태 및 CRUD 처리 */
export const useVendorEditor = ({ fetchList, create, update, remove }) => {
  const [formData, setFormData] = useState(EMPTY_DATA);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submittingAction, setSubmittingAction] = useState(null);
  const submittingRef = useRef(false);

  const beginSubmitting = useCallback((action) => {
    if (submittingRef.current) return false;
    submittingRef.current = true;
    setIsSubmitting(true);
    setSubmittingAction(action);
    return true;
  }, []);

  const endSubmitting = useCallback(() => {
    submittingRef.current = false;
    setIsSubmitting(false);
    setSubmittingAction(null);
  }, []);

  const setSelected = useCallback((row) => {
    if (submittingRef.current) return;
    if (!row) {
      setFormData(EMPTY_DATA);
      return;
    }
    setFormData({
      vendorId: row.vendorId || '',
      name: row.name || '',
      contactName: row.contactName || '',
      contactPhone: row.contactPhone || '',
      contactEmail: row.contactEmail || '',
      memo: row.memo || '',
    });
  }, []);

  const reset = useCallback(() => {
    if (submittingRef.current) return;
    setFormData(EMPTY_DATA);
  }, []);

  const handleChange = useCallback((e) => {
    if (submittingRef.current) return;
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const validateRequired = useCallback(() => {
    if (!formData.name || !formData.name.trim()) {
      winiMsg.showAlert('공급사명 은/는 필수입력값입니다.');
      return false;
    }
    return true;
  }, [formData]);

  const toPayload = useCallback(() => ({
    name: formData.name,
    contactName: formData.contactName || undefined,
    contactPhone: formData.contactPhone || undefined,
    contactEmail: formData.contactEmail || undefined,
    memo: formData.memo || undefined,
  }), [formData]);

  const handleCreate = useCallback(async () => {
    if (formData.vendorId) {
      await winiMsg.showAlert('이미 공급사가 선택되어 있습니다.\n초기화를 진행하고 등록해주세요.');
      return;
    }
    if (!validateRequired()) return;
    if (!beginSubmitting('create')) return;

    try {
      const answer = await winiMsg.showConfirm('등록 하시겠습니까?');
      if (answer !== 'Y') return;

      const data = await create(toPayload());
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('등록 되었습니다.');
        setFormData(EMPTY_DATA);
        await fetchList();
      } else {
        winiMsg.showSnackbar(data?.message || '등록 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('등록 중 오류가 발생했습니다.');
    } finally {
      endSubmitting();
    }
  }, [formData, validateRequired, beginSubmitting, create, toPayload, fetchList, endSubmitting]);

  const handleUpdate = useCallback(async () => {
    if (!formData.vendorId) {
      await winiMsg.showAlert('수정할 공급사를 선택해주세요.');
      return;
    }
    if (!validateRequired()) return;
    if (!beginSubmitting('update')) return;

    try {
      const answer = await winiMsg.showConfirm('수정 하시겠습니까?');
      if (answer !== 'Y') return;

      const data = await update(formData.vendorId, toPayload());
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('수정 되었습니다.');
        await fetchList();
      } else {
        winiMsg.showSnackbar(data?.message || '수정 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('수정 중 오류가 발생했습니다.');
    } finally {
      endSubmitting();
    }
  }, [formData, validateRequired, beginSubmitting, update, toPayload, fetchList, endSubmitting]);

  const handleDelete = useCallback(async () => {
    if (!formData.vendorId) {
      await winiMsg.showAlert('삭제할 공급사를 선택해주세요.');
      return;
    }
    if (!beginSubmitting('delete')) return;

    try {
      const answer = await winiMsg.showConfirm('삭제 하시겠습니까?');
      if (answer !== 'Y') return;

      const data = await remove(formData.vendorId);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('삭제 되었습니다.');
        setFormData(EMPTY_DATA);
        await fetchList();
      } else {
        winiMsg.showSnackbar(data?.message || '삭제 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('삭제 중 오류가 발생했습니다.');
    } finally {
      endSubmitting();
    }
  }, [formData, beginSubmitting, remove, fetchList, endSubmitting]);

  return {
    formData,
    isSubmitting,
    submittingAction,
    setSelected,
    reset,
    handleChange,
    handleCreate,
    handleUpdate,
    handleDelete,
  };
};
