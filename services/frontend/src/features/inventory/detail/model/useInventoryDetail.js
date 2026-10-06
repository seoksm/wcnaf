import { useCallback, useRef, useState } from 'react';
import {
  fetchInventoryProgress,
  fetchInventoryResults,
  approveInventoryResult,
  rejectInventoryResult,
  adminConfirmInventoryResult,
  adminReportInventoryAnomaly,
  closeInventoryResult,
  closeInventoryResultsBulk,
  closeInventory,
} from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const CLOSE_FORM_EMPTY = { closureAction: '', closureReasonCode: '', note: '' };
const ANOMALY_FORM_EMPTY = { anomalyType: '', note: '' };

/**
 * S-303(진행 현황) · S-304(검수 승인/반려) · S-308(종결 처리) - 목록 하나(상태 필터)와 그 안에서
 * 선택한 행에 대한 액션들을 한 훅에서 관리한다. 임직원 모바일 검수(S-310/311)가 아직 없어, 관리자
 * 대체 확인/이상보고가 실제 확인 입력 경로를 대신한다.
 */
export const useInventoryDetail = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [inventoryId, setInventoryId] = useState(null);
  const [progress, setProgress] = useState(null);
  const [results, setResults] = useState([]);
  const [statusFilter, setStatusFilter] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedRow, setSelectedRow] = useState(null);
  const [checkedRows, setCheckedRows] = useState([]);
  const [anomalyForm, setAnomalyForm] = useState(ANOMALY_FORM_EMPTY);
  const [closeForm, setCloseForm] = useState(CLOSE_FORM_EMPTY);
  const [isActing, setIsActing] = useState(false);
  const actingRef = useRef(false);

  const load = useCallback(async (id, status) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !id) return;
    setIsLoading(true);
    setError(null);
    try {
      const [progressData, resultsData] = await Promise.all([
        fetchInventoryProgress(currentConnector, id),
        fetchInventoryResults(currentConnector, id, status),
      ]);
      const progressOk = progressData?.result === 'SUCCESS';
      const resultsOk = resultsData?.result === 'SUCCESS';
      if (progressOk) setProgress(progressData.data);
      if (resultsOk) setResults(Array.isArray(resultsData.data) ? resultsData.data : []);
      if (!progressOk || !resultsOk) {
        const message = progressData?.message || resultsData?.message || '진행 현황 조회에 실패했습니다.';
        setError(message);
        winiMsg.showSnackbar(message);
      }
    } catch {
      const message = '진행 현황 조회 중 오류가 발생했습니다.';
      setError(message);
      winiMsg.showSnackbar(message);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const select = useCallback((id) => {
    setInventoryId(id);
    setStatusFilter('');
    setSelectedRow(null);
    setCheckedRows([]);
    // 이전 자산의 진행 현황·목록이 새 데이터가 도착할 때까지 남아 보이지 않도록 먼저 비운다.
    setProgress(null);
    setResults([]);
    setError(null);
    load(id, '');
  }, [load]);

  const close = useCallback(() => {
    setInventoryId(null);
    setProgress(null);
    setResults([]);
    setError(null);
    setSelectedRow(null);
    setCheckedRows([]);
  }, []);

  const onStatusFilterChange = useCallback((e) => {
    const status = e.target.value;
    setStatusFilter(status);
    setSelectedRow(null);
    load(inventoryId, status);
  }, [inventoryId, load]);

  const refresh = useCallback(() => load(inventoryId, statusFilter), [load, inventoryId, statusFilter]);

  const onAnomalyFormChange = useCallback((e) => {
    const { name, value } = e.target;
    setAnomalyForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  const onCloseFormChange = useCallback((e) => {
    const { name, value } = e.target;
    setCloseForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  const runAction = useCallback(async (action, successMessage) => {
    if (actingRef.current || !connectorRef.current) return;
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await action();
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar(successMessage);
        setSelectedRow(null);
        setCheckedRows([]);
        setAnomalyForm(ANOMALY_FORM_EMPTY);
        setCloseForm(CLOSE_FORM_EMPTY);
        await refresh();
        return true;
      }
      winiMsg.showSnackbar(data?.message || '처리 중 오류가 발생했습니다.');
      return false;
    } catch (err) {
      winiMsg.showSnackbar(err?.response?.data?.message || '처리 중 오류가 발생했습니다.');
      return false;
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [refresh]);

  const adminConfirm = useCallback(async (inventoryTargetId) => {
    const answer = await winiMsg.showConfirm('관리자가 대신 확인 처리하시겠습니까?');
    if (answer !== 'Y') return;
    await runAction(() => adminConfirmInventoryResult(connectorRef.current, inventoryTargetId), '확인 처리 되었습니다.');
  }, [runAction]);

  const adminAnomaly = useCallback(async (inventoryTargetId) => {
    if (!anomalyForm.anomalyType) {
      winiMsg.showAlert('이상 유형을 선택해주세요.');
      return;
    }
    await runAction(
      () => adminReportInventoryAnomaly(connectorRef.current, inventoryTargetId, anomalyForm.anomalyType, anomalyForm.note),
      '이상 보고 되었습니다.',
    );
  }, [anomalyForm, runAction]);

  const approve = useCallback(async (inventoryTargetId) => {
    const answer = await winiMsg.showConfirm('승인하시겠습니까?');
    if (answer !== 'Y') return;
    await runAction(() => approveInventoryResult(connectorRef.current, inventoryTargetId), '승인 되었습니다.');
  }, [runAction]);

  const reject = useCallback(async (inventoryTargetId, reason) => {
    const answer = await winiMsg.showConfirm('반려하시겠습니까?\n임직원은 다시 확인해야 합니다.');
    if (answer !== 'Y') return;
    await runAction(() => rejectInventoryResult(connectorRef.current, inventoryTargetId, reason), '반려 되었습니다.');
  }, [runAction]);

  const validateCloseForm = useCallback(() => {
    if (!closeForm.closureAction || !closeForm.closureReasonCode) {
      winiMsg.showAlert('종결 처리 방법과 사유는 필수입니다.');
      return false;
    }
    return true;
  }, [closeForm]);

  const closeOne = useCallback(async (inventoryTargetId) => {
    if (!validateCloseForm()) return;
    const answer = await winiMsg.showConfirm('이 항목을 종결 처리하시겠습니까?');
    if (answer !== 'Y') return;
    await runAction(
      () => closeInventoryResult(connectorRef.current, inventoryTargetId, closeForm.closureAction, closeForm.closureReasonCode, closeForm.note),
      '종결 처리 되었습니다.',
    );
  }, [closeForm, runAction, validateCloseForm]);

  const closeBulk = useCallback(async () => {
    if (!checkedRows || checkedRows.length === 0) {
      winiMsg.showAlert('일괄 종결 처리할 대상을 선택해주세요.');
      return;
    }
    if (!validateCloseForm()) return;
    if (closeForm.closureAction === 'LOST') {
      winiMsg.showAlert('분실 처리는 한 건씩 개별로 진행해주세요.');
      return;
    }
    const answer = await winiMsg.showConfirm(`선택한 ${checkedRows.length}건을 일괄 종결 처리하시겠습니까?`);
    if (answer !== 'Y') return;
    await runAction(
      () => closeInventoryResultsBulk(
        connectorRef.current, checkedRows.map((r) => r.inventoryTargetId), closeForm.closureAction, closeForm.closureReasonCode, closeForm.note,
      ),
      '일괄 종결 처리 되었습니다.',
    );
  }, [checkedRows, closeForm, runAction, validateCloseForm]);

  const finalizeInventory = useCallback(async () => {
    const answer = await winiMsg.showConfirm('조사를 종료 확정하시겠습니까?\n종료 후에는 검수 결과를 바꿀 수 없습니다.');
    if (answer !== 'Y') return;
    await runAction(() => closeInventory(connectorRef.current, inventoryId), '조사가 종료되었습니다.');
  }, [inventoryId, runAction]);

  return {
    inventoryId,
    progress,
    results,
    statusFilter,
    isLoading,
    error,
    selectedRow,
    setSelectedRow,
    checkedRows,
    onCheckSelectionChange: setCheckedRows,
    anomalyForm,
    onAnomalyFormChange,
    closeForm,
    onCloseFormChange,
    isActing,
    select,
    close,
    onStatusFilterChange,
    adminConfirm,
    adminAnomaly,
    approve,
    reject,
    closeOne,
    closeBulk,
    finalizeInventory,
  };
};
