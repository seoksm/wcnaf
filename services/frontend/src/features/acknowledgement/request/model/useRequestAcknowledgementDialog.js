import { useCallback, useRef, useState } from 'react';
import { createAcknowledgement } from '@/entities/acknowledgement';
import { fetchCommonUsers, fetchTangibleAssets } from '@/entities/tangibleAsset';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_FORM = { assetCode: '', tangibleAssetId: '', assetName: '', memberId: '', type: 'RECEIPT', managerName: '' };

/** S-430 확인서 요청 - S-412(대여 관리자 대행)와 동일한 방식으로 자산코드를 조회해 확정한다 */
export const useRequestAcknowledgementDialog = ({ onSuccess }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);
  const [userList, setUserList] = useState([]);
  const [isResolving, setIsResolving] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const submittingRef = useRef(false);

  const openDialog = useCallback(async () => {
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
    if (!form.tangibleAssetId) {
      winiMsg.showAlert('자산코드로 조회해 대상을 확인해주세요.');
      return;
    }
    if (!form.memberId) {
      winiMsg.showAlert('대상자를 선택해주세요.');
      return;
    }
    const memberName = userList.find((u) => u.id === form.memberId)?.fullName
      || userList.find((u) => u.id === form.memberId)?.username;

    submittingRef.current = true;
    setIsSubmitting(true);
    try {
      const data = await createAcknowledgement(connectorRef.current, {
        tangibleAssetId: form.tangibleAssetId,
        memberId: form.memberId,
        memberName,
        type: form.type,
        managerName: form.managerName || undefined,
      });
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('확인서가 요청되었습니다.');
        setOpen(false);
        await onSuccess?.();
      } else {
        winiMsg.showSnackbar(data?.message || '확인서 요청 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('확인서 요청 중 오류가 발생했습니다.');
    } finally {
      submittingRef.current = false;
      setIsSubmitting(false);
    }
  }, [form, userList, onSuccess]);

  return { open, form, userList, isResolving, isSubmitting, openDialog, closeDialog, handleChange, resolveAsset, submit };
};
