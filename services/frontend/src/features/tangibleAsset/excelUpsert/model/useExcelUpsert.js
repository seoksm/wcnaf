import { useCallback, useRef, useState } from 'react';
import {
  fetchTangibleAssetExcelTemplate,
  previewTangibleAssetExcelUpsert,
  commitTangibleAssetExcelUpsert,
} from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

/**
 * 유형자산 엑셀 업서트 - 양식 다운로드 · 미리보기 · 확정 (S-213)
 */
export const useExcelUpsert = ({ fetchList }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [open, setOpen] = useState(false);
  const [file, setFile] = useState(null);
  const [rows, setRows] = useState([]);
  const [isPreviewing, setIsPreviewing] = useState(false);
  const [isCommitting, setIsCommitting] = useState(false);
  const [summary, setSummary] = useState(null);
  // "확정" 한 번의 시도를 식별하는 키 - 같은 미리보기 결과로 재시도(네트워크 오류 등)해도 이 값은
  // 그대로 유지해 서버가 중복 반영을 막을 수 있게 하고, 새로 미리보기하면 새 값으로 바뀐다.
  const [commitId, setCommitId] = useState(null);
  const previewingRef = useRef(false);
  const committingRef = useRef(false);

  const openDialog = useCallback(() => {
    setFile(null);
    setRows([]);
    setSummary(null);
    setCommitId(null);
    setOpen(true);
  }, []);

  const closeDialog = useCallback(() => setOpen(false), []);

  const onFileChange = useCallback((e) => {
    const selected = e.target.files?.[0] || null;
    setFile(selected);
    setRows([]);
    setSummary(null);
    setCommitId(null);
  }, []);

  const downloadTemplate = useCallback(async () => {
    if (!connectorRef.current) return;
    try {
      const blob = await fetchTangibleAssetExcelTemplate(connectorRef.current);
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = '유형자산_등록양식.xlsx';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch {
      winiMsg.showSnackbar('양식 다운로드 중 오류가 발생했습니다.');
    }
  }, []);

  const runPreview = useCallback(async () => {
    if (!connectorRef.current || previewingRef.current || committingRef.current) return;
    if (!file) {
      winiMsg.showAlert('업로드할 엑셀 파일을 선택해주세요.');
      return;
    }

    previewingRef.current = true;
    setIsPreviewing(true);
    try {
      const data = await previewTangibleAssetExcelUpsert(connectorRef.current, file);
      if (data?.result === 'SUCCESS') {
        setRows(Array.isArray(data.data) ? data.data : []);
        setSummary(null);
        setCommitId(crypto.randomUUID());
      } else {
        winiMsg.showSnackbar(data?.message || '미리보기 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('미리보기 중 오류가 발생했습니다. 양식을 확인해주세요.');
    } finally {
      previewingRef.current = false;
      setIsPreviewing(false);
    }
  }, [file]);

  const runCommit = useCallback(async () => {
    if (!connectorRef.current || !commitId || committingRef.current || previewingRef.current) return;
    const applicableCount = rows.filter((r) => r.action === 'CREATE' || r.action === 'UPDATE').length;
    if (applicableCount === 0) {
      winiMsg.showAlert('반영할 수 있는 행이 없습니다.');
      return;
    }

    committingRef.current = true;
    const answer = await winiMsg.showConfirm(`${applicableCount}건을 등록/수정하시겠습니까? (오류 행은 제외됩니다)`);
    if (answer !== 'Y') {
      committingRef.current = false;
      return;
    }

    setIsCommitting(true);
    try {
      const data = await commitTangibleAssetExcelUpsert(connectorRef.current, commitId, rows);
      if (data?.result === 'SUCCESS') {
        setSummary(data.data);
        winiMsg.showSnackbar('엑셀 업서트가 완료되었습니다.');
        await fetchList?.();
      } else {
        winiMsg.showSnackbar(data?.message || '확정 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('확정 중 오류가 발생했습니다.');
    } finally {
      committingRef.current = false;
      setIsCommitting(false);
    }
  }, [rows, fetchList, commitId]);

  return {
    open,
    file,
    rows,
    isPreviewing,
    isCommitting,
    summary,
    openDialog,
    closeDialog,
    onFileChange,
    downloadTemplate,
    runPreview,
    runCommit,
  };
};
