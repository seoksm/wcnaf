import { useCallback, useRef, useState } from 'react';
import { winiMsg } from '@/shared/model';
import { winiDate } from '@/shared/lib';
import { requiresMember } from '@/entities/tangibleAsset';

const EMPTY_DATA = {
  tangibleAssetId: '',
  assetCode: '',
  assetName: '',
  categoryId: '',
  locationId: '',
  lifeStatus: 'USE',
  assignType: 'UNASSIGNED',
  acquisitionDate: '',
  acquisitionAmount: '',
  modelName: '',
  manufacturer: '',
  serialNo: '',
  currentMemberId: '',
  memo: '',
};

/**
 * 유형자산 등록/수정 폼 상태 및 CRUD 처리
 */
export const useTangibleAssetEditor = (params) => {
  const { fetchList, create, update, isOptionsReady = true } = params;

  const [formData, setFormData] = useState(EMPTY_DATA);
  const [isSaving, setIsSaving] = useState(false);
  const savingRef = useRef(false);

  const setSelected = useCallback((row) => {
    if (!row) {
      setFormData(EMPTY_DATA);
      return;
    }
    setFormData({
      tangibleAssetId: row.tangibleAssetId || '',
      assetCode: row.assetCode || '',
      assetName: row.assetName || '',
      categoryId: row.categoryId || '',
      locationId: row.locationId || '',
      lifeStatus: row.lifeStatus || 'USE',
      assignType: row.assignType || 'UNASSIGNED',
      acquisitionDate: row.acquisitionDate
        ? winiDate.dateFormat(winiDate(row.acquisitionDate), 'YYYY-MM-DD')
        : '',
      acquisitionAmount: row.acquisitionAmount ?? '',
      modelName: row.modelName || '',
      manufacturer: row.manufacturer || '',
      serialNo: row.serialNo || '',
      currentMemberId: row.currentMemberId || '',
      memo: row.memo || '',
    });
  }, []);

  const reset = useCallback(() => {
    setFormData(EMPTY_DATA);
  }, []);

  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    setFormData((prev) => {
      const next = { ...prev, [name]: value };
      // 배정 형태가 사용자를 필요로 하지 않는 값으로 바뀌면 배정 사용자 선택을 비운다
      if (name === 'assignType' && !requiresMember(value)) {
        next.currentMemberId = '';
      }
      return next;
    });
  }, []);

  const validateRequired = useCallback(() => {
    if (!formData.assetName || !formData.assetName.trim()) {
      winiMsg.showAlert('자산명 은/는 필수입력값입니다.');
      return false;
    }
    if (!formData.categoryId) {
      winiMsg.showAlert('자산 종류 은/는 필수입력값입니다.');
      return false;
    }
    if (!formData.locationId) {
      winiMsg.showAlert('자산 위치 은/는 필수입력값입니다.');
      return false;
    }
    if (!formData.acquisitionDate) {
      winiMsg.showAlert('취득일 은/는 필수입력값입니다.');
      return false;
    }
    if (
      formData.acquisitionAmount === '' ||
      formData.acquisitionAmount == null
    ) {
      winiMsg.showAlert('취득가액 은/는 필수입력값입니다.');
      return false;
    }
    if (requiresMember(formData.assignType) && !formData.currentMemberId) {
      winiMsg.showAlert(
        '개인배정/대여중 상태는 배정 사용자를 선택해야 합니다.',
      );
      return false;
    }
    return true;
  }, [formData]);

  const toPayload = useCallback(
    () => ({
      assetName: formData.assetName,
      categoryId: formData.categoryId,
      locationId: formData.locationId,
      lifeStatus: formData.lifeStatus,
      assignType: formData.assignType,
      // 순수 날짜를 정오(UTC) 기준으로 고정해 타임존에 따른 날짜 밀림을 방지한다
      acquisitionDate: `${formData.acquisitionDate}T12:00:00Z`,
      acquisitionAmount: Number(formData.acquisitionAmount),
      modelName: formData.modelName || null,
      manufacturer: formData.manufacturer || null,
      serialNo: formData.serialNo || null,
      currentMemberId: requiresMember(formData.assignType)
        ? formData.currentMemberId
        : null,
      memo: formData.memo || null,
    }),
    [formData],
  );

  const handleCreate = useCallback(async () => {
    if (savingRef.current) return;
    if (!isOptionsReady) {
      await winiMsg.showAlert(
        '자산 종류·위치·사용자 옵션을 불러온 후 등록해주세요.',
      );
      return;
    }
    if (formData.tangibleAssetId) {
      await winiMsg.showAlert(
        '이미 유형자산이 선택되어 있습니다.\n초기화를 진행하고 등록해주세요.',
      );
      return;
    }
    if (!validateRequired()) return;

    savingRef.current = true;
    const answer = await winiMsg.showConfirm('등록 하시겠습니까?');
    if (answer !== 'Y') {
      savingRef.current = false;
      return;
    }

    setIsSaving(true);
    try {
      const data = await create(toPayload());
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('등록 되었습니다.');
        reset();
        await fetchList();
      } else {
        winiMsg.showSnackbar(data?.message || '등록 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('등록 중 오류가 발생했습니다.');
    } finally {
      savingRef.current = false;
      setIsSaving(false);
    }
  }, [
    isOptionsReady,
    formData,
    validateRequired,
    create,
    toPayload,
    fetchList,
    reset,
  ]);

  const handleUpdate = useCallback(async () => {
    if (savingRef.current) return;
    if (!isOptionsReady) {
      await winiMsg.showAlert(
        '자산 종류·위치·사용자 옵션을 불러온 후 수정해주세요.',
      );
      return;
    }
    if (!formData.tangibleAssetId) {
      await winiMsg.showAlert('수정할 유형자산을 선택해주세요.');
      return;
    }
    if (!validateRequired()) return;

    savingRef.current = true;
    const answer = await winiMsg.showConfirm('수정 하시겠습니까?');
    if (answer !== 'Y') {
      savingRef.current = false;
      return;
    }

    setIsSaving(true);
    try {
      const data = await update(formData.tangibleAssetId, toPayload());
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('수정 되었습니다.');
        await fetchList();
      } else {
        winiMsg.showSnackbar(data?.message || '수정 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('수정 중 오류가 발생했습니다.');
    } finally {
      savingRef.current = false;
      setIsSaving(false);
    }
  }, [
    isOptionsReady,
    formData,
    validateRequired,
    update,
    toPayload,
    fetchList,
  ]);

  return {
    formData,

    setSelected,
    reset,
    handleChange,
    handleCreate,
    handleUpdate,
    isSaving,
  };
};
