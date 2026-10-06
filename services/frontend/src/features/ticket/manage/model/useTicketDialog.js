import { useCallback, useRef, useState } from 'react';
import { createTicketByAdmin } from '@/entities/ticket';
import { fetchCommonUsers, fetchTangibleAssets } from '@/entities/tangibleAsset';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_FORM = {
  ticketType: 'REPAIR', title: '', content: '', requestedBy: '',
  assetCode: '', tangibleAssetId: '', assetName: '',
};

/** S-603 티켓 등록(관리자) - P-6 모달 */
export const useTicketDialog = ({ onSuccess }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);
  const [userList, setUserList] = useState([]);
  const [isResolving, setIsResolving] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const submittingRef = useRef(false);

  const openCreate = useCallback(async () => {
    setForm(EMPTY_FORM);
    setOpen(true);
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    try {
      const data = await fetchCommonUsers(currentConnector);
      if (data?.result === 'SUCCESS') setUserList(data.data || []);
    } catch {
      winiMsg.showSnackbar('사용자 목록 조회 중 오류가 발생했습니다.');
    }
  }, []);

  const closeDialog = useCallback(() => setOpen(false), []);

  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value, ...(name === 'assetCode' ? { tangibleAssetId: '', assetName: '' } : {}) }));
  }, []);

  const resolveAsset = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !form.assetCode?.trim()) return;
    setIsResolving(true);
    try {
      const data = await fetchTangibleAssets(currentConnector, { keyword: form.assetCode.trim(), page: 0, size: 5 });
      const matched = (data?.data?.content || []).find((a) => a.assetCode === form.assetCode.trim());
      if (matched) {
        setForm((prev) => ({ ...prev, tangibleAssetId: matched.tangibleAssetId, assetName: matched.assetName }));
      } else {
        setForm((prev) => ({ ...prev, tangibleAssetId: '', assetName: '' }));
        winiMsg.showSnackbar('자산코드를 찾을 수 없습니다.');
      }
    } catch {
      winiMsg.showSnackbar('자산 조회 중 오류가 발생했습니다.');
    } finally {
      setIsResolving(false);
    }
  }, [form.assetCode]);

  const submit = useCallback(async () => {
    if (submittingRef.current) return;
    if (!form.title.trim()) {
      winiMsg.showAlert('제목은 필수입니다.');
      return;
    }
    if (!form.requestedBy) {
      winiMsg.showAlert('요청자를 선택해주세요.');
      return;
    }
    const body = {
      ticketType: form.ticketType,
      title: form.title,
      content: form.content || undefined,
      tangibleAssetId: form.tangibleAssetId || undefined,
      requestedBy: form.requestedBy,
    };

    submittingRef.current = true;
    setIsSubmitting(true);
    try {
      const data = await createTicketByAdmin(connectorRef.current, body);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('등록되었습니다.');
        setOpen(false);
        await onSuccess?.();
      } else {
        winiMsg.showSnackbar(data?.message || '등록 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('등록 중 오류가 발생했습니다.');
    } finally {
      submittingRef.current = false;
      setIsSubmitting(false);
    }
  }, [form, onSuccess]);

  return { open, form, userList, isResolving, isSubmitting, openCreate, closeDialog, handleChange, resolveAsset, submit };
};
