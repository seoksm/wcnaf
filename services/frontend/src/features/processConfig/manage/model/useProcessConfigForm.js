import { useCallback, useState } from 'react';
import { fetchProcessConfig, updateProcessConfig } from '@/entities/processConfig';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

const toNumberOrNull = (value) => (value === '' || value === null || value === undefined ? null : Number(value));

/**
 * S-400 프로세스 설정 - 대여/수령·반납 On-Off와 부속 정책을 조회·수정한다. 워크스페이스당
 * 단일 행이라 목록 없이 폼 하나만 있다(P-3). §1 파급 매트릭스에 따라 각 토글이 켜져야만
 * 그 부속 정책 필드가 폼에 나타난다(Editor.jsx의 requiresMember 조건부 렌더링과 동일한 방식).
 */
export const useProcessConfigForm = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');

  const [form, setForm] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);

  const load = useCallback(async () => {
    if (!connector) return;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuId && currentMenuId !== formMenuId) return;

    setIsLoading(true);
    try {
      const data = await fetchProcessConfig(connector);
      if (data?.result === 'SUCCESS') {
        setForm(data.data);
      } else {
        winiMsg.showSnackbar(data?.message || '프로세스 설정 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('프로세스 설정 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, [connector, formMenuId]);

  useInitialFetch(load, Boolean(connector));

  const setField = useCallback((field, value) => {
    setForm((prev) => (prev ? { ...prev, [field]: value } : prev));
  }, []);

  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    setField(name, value);
  }, [setField]);

  const handleToggle = useCallback((field) => (_event, checked) => {
    setField(field, checked);
  }, [setField]);

  const save = useCallback(async () => {
    if (!form || isSaving) return;
    setIsSaving(true);
    try {
      const payload = {
        ...form,
        defaultLoanDays: toNumberOrNull(form.defaultLoanDays),
        maxExtendCount: toNumberOrNull(form.maxExtendCount),
        concurrentLimit: toNumberOrNull(form.concurrentLimit),
        approvalDueDays: toNumberOrNull(form.approvalDueDays),
        remindIntervalDays: toNumberOrNull(form.remindIntervalDays),
      };
      const data = await updateProcessConfig(connector, payload);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('프로세스 설정이 저장되었습니다.');
        await load();
      } else {
        winiMsg.showSnackbar(data?.message || '프로세스 설정 저장 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('프로세스 설정 저장 중 오류가 발생했습니다.');
    } finally {
      setIsSaving(false);
    }
  }, [connector, form, isSaving, load]);

  return { form, isLoading, isSaving, handleChange, handleToggle, save, reload: load };
};
