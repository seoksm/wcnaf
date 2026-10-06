import { useCallback, useRef, useState } from 'react';
import { createLoanByAdmin } from '@/entities/loan';
import { fetchCommonUsers, fetchTangibleAssets } from '@/entities/tangibleAsset';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_FORM = { assetCode: '', tangibleAssetId: '', assetName: '', memberId: '' };

/**
 * S-412 대여 처리(관리자 대행) - 자산코드로 대상을 조회해 확정한 뒤 멤버를 선택해 대여를 생성한다.
 * 별도의 자산 선택 자동완성 대신, 이미 있는 자산 검색(키워드)에서 정확히 일치하는 코드를 찾는
 * 방식으로 새 엔드포인트 없이 처리한다.
 */
export const useAdminBorrowDialog = ({ onSuccess }) => {
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
      winiMsg.showAlert('대여자를 선택해주세요.');
      return;
    }
    submittingRef.current = true;
    setIsSubmitting(true);
    try {
      const data = await createLoanByAdmin(connectorRef.current, { tangibleAssetId: form.tangibleAssetId, memberId: form.memberId });
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('대여 처리되었습니다.');
        setOpen(false);
        await onSuccess?.();
      } else {
        winiMsg.showSnackbar(data?.message || '대여 처리 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('대여 처리 중 오류가 발생했습니다.');
    } finally {
      submittingRef.current = false;
      setIsSubmitting(false);
    }
  }, [form, onSuccess]);

  return { open, form, userList, isResolving, isSubmitting, openDialog, closeDialog, handleChange, resolveAsset, submit };
};
