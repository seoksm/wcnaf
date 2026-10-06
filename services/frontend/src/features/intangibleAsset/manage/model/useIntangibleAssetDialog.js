import { useCallback, useRef, useState } from 'react';
import { createIntangibleAsset, updateIntangibleAsset } from '@/entities/intangibleAsset';
import { fetchCommonUsers } from '@/entities/tangibleAsset';
import { winiCom, winiDate } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_FORM = {
  intangibleAssetId: '', intangibleType: 'DOMAIN', name: '', issuer: '',
  registeredDate: '', expiryDate: '', ownerMemberId: '', alertDays: '30,7,3,1', memo: '',
};

/** S-501 무형자산 등록/수정 - P-6 모달로 구현(별도 라우트 대신 S-500 목록 안에서 처리) */
export const useIntangibleAssetDialog = ({ onSuccess }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);
  const [userList, setUserList] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const loadUsers = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    try {
      const data = await fetchCommonUsers(currentConnector);
      if (data?.result === 'SUCCESS') setUserList(data.data || []);
    } catch {
      winiMsg.showSnackbar('사용자 목록 조회 중 오류가 발생했습니다.');
    }
  }, []);

  const openCreate = useCallback(() => {
    setForm(EMPTY_FORM);
    setOpen(true);
    loadUsers();
  }, [loadUsers]);

  const openEdit = useCallback((row) => {
    setForm({
      intangibleAssetId: row.intangibleAssetId,
      intangibleType: row.intangibleType,
      name: row.name || '',
      issuer: row.issuer || '',
      registeredDate: row.registeredDate || '',
      expiryDate: row.expiryDate || '',
      ownerMemberId: row.ownerMemberId || '',
      alertDays: row.alertDays || '30,7,3,1',
      memo: row.memo || '',
    });
    setOpen(true);
    loadUsers();
  }, [loadUsers]);

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
    if (!form.name.trim()) {
      winiMsg.showAlert('자산명은 필수입니다.');
      return;
    }
    if (!form.expiryDate) {
      winiMsg.showAlert('만료일은 필수입니다.');
      return;
    }
    const body = {
      intangibleType: form.intangibleType,
      name: form.name,
      issuer: form.issuer || undefined,
      registeredDate: form.registeredDate || undefined,
      expiryDate: form.expiryDate,
      ownerMemberId: form.ownerMemberId || undefined,
      alertDays: form.alertDays || undefined,
      memo: form.memo || undefined,
    };

    setIsSubmitting(true);
    try {
      const data = form.intangibleAssetId
        ? await updateIntangibleAsset(connectorRef.current, form.intangibleAssetId, body)
        : await createIntangibleAsset(connectorRef.current, body);

      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar(form.intangibleAssetId ? '수정되었습니다.' : '등록되었습니다.');
        setOpen(false);
        await onSuccess?.();
      } else {
        winiMsg.showSnackbar(data?.message || '저장 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('저장 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  }, [form, onSuccess]);

  return { open, form, userList, isSubmitting, openCreate, openEdit, closeDialog, handleChange, handleDateChange, submit };
};
