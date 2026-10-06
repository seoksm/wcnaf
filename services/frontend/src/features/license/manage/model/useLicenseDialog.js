import { useCallback, useRef, useState } from 'react';
import { createLicense, updateLicense } from '@/entities/license';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_FORM = { licenseId: '', name: '', memo: '' };

/** S-531 라이선스 등록/수정 - P-6 모달로 구현(S-530 목록 안에서 처리) */
export const useLicenseDialog = ({ onSuccess }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const openCreate = useCallback(() => {
    setForm(EMPTY_FORM);
    setOpen(true);
  }, []);

  const openEdit = useCallback((row) => {
    setForm({
      licenseId: row.licenseId,
      name: row.name || '',
      memo: row.memo || '',
    });
    setOpen(true);
  }, []);

  const closeDialog = useCallback(() => setOpen(false), []);

  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  const submit = useCallback(async () => {
    if (!form.name.trim()) {
      winiMsg.showAlert('라이선스명은 필수입니다.');
      return;
    }
    const body = { name: form.name, memo: form.memo || undefined };

    setIsSubmitting(true);
    try {
      const data = form.licenseId
        ? await updateLicense(connectorRef.current, form.licenseId, body)
        : await createLicense(connectorRef.current, body);

      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar(form.licenseId ? '수정되었습니다.' : '등록되었습니다.');
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

  return { open, form, isSubmitting, openCreate, openEdit, closeDialog, handleChange, submit };
};
