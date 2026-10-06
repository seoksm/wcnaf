import { useCallback, useRef, useState } from 'react';
import { winiMsg } from '@/shared/model';

const EMPTY_DATA = {
  categoryId: '',
  categoryCode: '',
  categoryName: '',
  sortSeq: '',
  usefulLifeMonths: '',
  residualRate: '',
  memorandumValue: '',
  editable: true,
};

/**
 * 자산 종류 등록/수정 폼 상태 및 CRUD 처리
 */
export const useAssetCategoryEditor = (params) => {
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
      categoryId: row.categoryId || '',
      categoryCode: row.categoryCode || '',
      categoryName: row.categoryName || '',
      sortSeq: row.sortSeq ?? '',
      usefulLifeMonths: row.usefulLifeMonths ?? '',
      residualRate: row.residualRate ?? '',
      memorandumValue: row.memorandumValue ?? '',
      editable: row.editable !== false,
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
    if (!formData.categoryCode || !formData.categoryCode.trim()) {
      winiMsg.showAlert('종류 코드 은/는 필수입력값입니다.');
      return false;
    }
    if (!formData.categoryName || !formData.categoryName.trim()) {
      winiMsg.showAlert('종류명 은/는 필수입력값입니다.');
      return false;
    }
    return true;
  }, [formData]);

  const toPayload = useCallback(
    () => ({
      sortSeq: formData.sortSeq === '' ? null : Number(formData.sortSeq),
      usefulLifeMonths:
        formData.usefulLifeMonths === ''
          ? null
          : Number(formData.usefulLifeMonths),
      residualRate:
        formData.residualRate === '' ? null : Number(formData.residualRate),
      memorandumValue:
        formData.memorandumValue === ''
          ? null
          : Number(formData.memorandumValue),
    }),
    [formData],
  );

  const handleCreate = useCallback(async () => {
    if (formData.categoryId) {
      await winiMsg.showAlert(
        '이미 자산 종류가 선택되어 있습니다.\n초기화를 진행하고 등록해주세요.',
      );
      return;
    }
    if (!validateRequired()) return;
    if (!beginSubmitting('create')) return;

    try {
      const answer = await winiMsg.showConfirm('등록 하시겠습니까?');
      if (answer !== 'Y') return;

      const data = await create({
        categoryCode: formData.categoryCode,
        categoryName: formData.categoryName,
        ...toPayload(),
      });
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
    if (!formData.categoryId) {
      await winiMsg.showAlert('수정할 자산 종류를 선택해주세요.');
      return;
    }
    if (!validateRequired()) return;
    if (!beginSubmitting('update')) return;

    try {
      const answer = await winiMsg.showConfirm('수정 하시겠습니까?');
      if (answer !== 'Y') return;

      const data = await update(formData.categoryId, {
        categoryName: formData.categoryName,
        ...toPayload(),
      });
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
    if (!formData.categoryId) {
      await winiMsg.showAlert('삭제할 자산 종류를 선택해주세요.');
      return;
    }
    if (formData.editable === false) {
      await winiMsg.showAlert('기본 제공 자산 종류는 삭제할 수 없습니다.');
      return;
    }
    if (!beginSubmitting('delete')) return;

    try {
      const answer = await winiMsg.showConfirm('삭제 하시겠습니까?');
      if (answer !== 'Y') return;

      const data = await remove(formData.categoryId);
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
    categoryCodeDisabled: !!formData.categoryId,
    deletable: !!formData.categoryId && formData.editable !== false,
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
