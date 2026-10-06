import { useCallback, useRef, useState } from 'react';
import { previewMemberInventory, registerMemberInventory, fetchCommonUsers } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_FORM = {
  title: '',
  approvalRequired: false,
  allowNewAssetRegistration: false,
  recurrenceRule: '',
  excludedMemberIds: [],
};

/** S-301 전수조사 생성(임직원형) - 대상 미리보기 → 생성 두 단계 */
export const useCreateMemberInventory = ({ fetchList }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState(EMPTY_FORM);
  const [userList, setUserList] = useState([]);
  const [preview, setPreview] = useState(null);
  const [isPreviewing, setIsPreviewing] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const submittingRef = useRef(false);

  const openDialog = useCallback(async (prefill) => {
    setFormData(prefill ? { ...EMPTY_FORM, ...prefill } : EMPTY_FORM);
    setPreview(null);
    setOpen(true);
    if (connectorRef.current) {
      try {
        const data = await fetchCommonUsers(connectorRef.current);
        if (data?.result === 'SUCCESS') {
          setUserList(Array.isArray(data?.data) ? data.data : []);
        }
      } catch {
        // 대상자 목록은 참고용이라 실패해도 다이얼로그 자체는 그대로 연다
      }
    }
  }, []);

  const closeDialog = useCallback(() => setOpen(false), []);

  const onChange = useCallback((e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const onToggle = useCallback((name) => (e) => {
    setFormData((prev) => ({ ...prev, [name]: e.target.checked }));
  }, []);

  const runPreview = useCallback(async () => {
    if (!connectorRef.current) return;
    setIsPreviewing(true);
    try {
      const data = await previewMemberInventory(connectorRef.current, formData.excludedMemberIds);
      if (data?.result === 'SUCCESS') {
        setPreview(data.data);
      } else {
        winiMsg.showSnackbar(data?.message || '대상 미리보기 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('대상 미리보기 중 오류가 발생했습니다.');
    } finally {
      setIsPreviewing(false);
    }
  }, [formData.excludedMemberIds]);

  const submit = useCallback(async () => {
    if (submittingRef.current || !connectorRef.current) return;
    if (!formData.title || !formData.title.trim()) {
      winiMsg.showAlert('조사명 은/는 필수입력값입니다.');
      return;
    }
    if (!preview) {
      winiMsg.showAlert('생성 전에 대상 미리보기를 먼저 확인해주세요.');
      return;
    }

    submittingRef.current = true;
    const answer = await winiMsg.showConfirm(
      `대상 자산 ${preview.targetAssetCount}건으로 전수조사를 생성하시겠습니까?\n생성 후에는 대상이 변경되지 않습니다.`,
    );
    if (answer !== 'Y') {
      submittingRef.current = false;
      return;
    }

    setIsSubmitting(true);
    try {
      const data = await registerMemberInventory(connectorRef.current, {
        title: formData.title,
        approvalRequired: formData.approvalRequired,
        allowNewAssetRegistration: formData.allowNewAssetRegistration,
        recurrenceRule: formData.recurrenceRule || null,
        excludedMemberIds: formData.excludedMemberIds,
      });
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('전수조사가 생성되었습니다.');
        closeDialog();
        await fetchList?.();
      } else {
        winiMsg.showSnackbar(data?.message || '전수조사 생성 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('전수조사 생성 중 오류가 발생했습니다.');
    } finally {
      submittingRef.current = false;
      setIsSubmitting(false);
    }
  }, [formData, preview, fetchList, closeDialog]);

  return {
    open,
    formData,
    userList,
    preview,
    isPreviewing,
    isSubmitting,
    openDialog,
    closeDialog,
    onChange,
    onToggle,
    runPreview,
    submit,
  };
};
