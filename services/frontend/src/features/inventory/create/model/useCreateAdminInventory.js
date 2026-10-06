import { useCallback, useRef, useState } from 'react';
import { previewAdminInventory, registerAdminInventory, fetchCommonUsers } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_FORM = {
  title: '',
  approvalRequired: false,
  allowNewAssetRegistration: false,
  recurrenceRule: '',
  inspectorMemberIds: [],
};

/** S-302 전수조사 생성(관리자형) - 공용·미배정 자산 대상, 검수자 풀 지정 필수(Q-32) */
export const useCreateAdminInventory = ({ fetchList }) => {
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
    const currentConnector = connectorRef.current;
    if (currentConnector) {
      try {
        const data = await fetchCommonUsers(currentConnector);
        if (data?.result === 'SUCCESS') {
          setUserList(Array.isArray(data?.data) ? data.data : []);
        }
      } catch {
        // 검수자 후보 목록은 참고용이라 실패해도 다이얼로그는 그대로 연다
      }
      runPreviewInternal(currentConnector);
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

  const runPreviewInternal = async (conn) => {
    setIsPreviewing(true);
    try {
      const data = await previewAdminInventory(conn);
      if (data?.result === 'SUCCESS') {
        setPreview(data.data);
      }
    } catch {
      // 미리보기 실패는 조용히 무시 - 대상 자산 수는 생성 시 서버가 다시 계산한다
    } finally {
      setIsPreviewing(false);
    }
  };

  const submit = useCallback(async () => {
    if (submittingRef.current || !connectorRef.current) return;
    if (!formData.title || !formData.title.trim()) {
      winiMsg.showAlert('조사명 은/는 필수입력값입니다.');
      return;
    }
    if (!formData.inspectorMemberIds || formData.inspectorMemberIds.length === 0) {
      winiMsg.showAlert('검수자를 1명 이상 선택해주세요.');
      return;
    }

    submittingRef.current = true;
    const answer = await winiMsg.showConfirm(
      `대상 자산 ${preview?.targetAssetCount ?? 0}건으로 전수조사를 생성하시겠습니까?\n생성 후에는 대상이 변경되지 않습니다.`,
    );
    if (answer !== 'Y') {
      submittingRef.current = false;
      return;
    }

    setIsSubmitting(true);
    try {
      const data = await registerAdminInventory(connectorRef.current, {
        title: formData.title,
        approvalRequired: formData.approvalRequired,
        allowNewAssetRegistration: formData.allowNewAssetRegistration,
        recurrenceRule: formData.recurrenceRule || null,
        inspectorMemberIds: formData.inspectorMemberIds,
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
    submit,
  };
};
