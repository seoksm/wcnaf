import { useCallback, useRef, useState } from 'react';
import { winiMsg } from '@/shared/model';

const EMPTY_DATA = {
  locationId: '',
  locationName: '',
  sortSeq: '',
};

/**
 * 자산 위치 등록/수정 폼 상태 및 CRUD 처리
 */
export const useAssetLocationEditor = (params) => {
  const { fetchList, create, update, remove } = params;

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
      locationId: row.locationId || '',
      locationName: row.locationName || '',
      sortSeq: row.sortSeq ?? '',
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
    if (!formData.locationName || !formData.locationName.trim()) {
      winiMsg.showAlert('위치명 은/는 필수입력값입니다.');
      return false;
    }
    return true;
  }, [formData]);

  const toPayload = useCallback(
    () => ({
      locationName: formData.locationName,
      sortSeq: formData.sortSeq === '' ? null : Number(formData.sortSeq),
    }),
    [formData],
  );

  const handleCreate = useCallback(async () => {
    if (formData.locationId) {
      await winiMsg.showAlert(
        '이미 자산 위치가 선택되어 있습니다.\n초기화를 진행하고 등록해주세요.',
      );
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
  }, [
    formData,
    validateRequired,
    beginSubmitting,
    create,
    toPayload,
    fetchList,
    endSubmitting,
  ]);

  const handleUpdate = useCallback(async () => {
    if (!formData.locationId) {
      await winiMsg.showAlert('수정할 자산 위치를 선택해주세요.');
      return;
    }
    if (!validateRequired()) return;
    if (!beginSubmitting('update')) return;

    try {
      const answer = await winiMsg.showConfirm('수정 하시겠습니까?');
      if (answer !== 'Y') return;

      const data = await update(formData.locationId, toPayload());
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
  }, [
    formData,
    validateRequired,
    beginSubmitting,
    update,
    toPayload,
    fetchList,
    endSubmitting,
  ]);

  const handleDelete = useCallback(async () => {
    if (!formData.locationId) {
      await winiMsg.showAlert('삭제할 자산 위치를 선택해주세요.');
      return;
    }
    if (!beginSubmitting('delete')) return;

    try {
      const answer = await winiMsg.showConfirm('삭제 하시겠습니까?');
      if (answer !== 'Y') return;

      const data = await remove(formData.locationId);
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
