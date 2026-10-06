import { useCallback, useRef, useState } from 'react';
import { duplicateTangibleAsset, batchModifyTangibleAsset } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';
import { requiresMember } from '@/entities/tangibleAsset';

const DUPLICATE_EMPTY = { count: '1' };

const BATCH_EMPTY = {
  enableCategory: false,
  categoryId: '',
  enableLocation: false,
  locationId: '',
  enableLifeStatus: false,
  lifeStatus: 'USE',
  enableAssignType: false,
  assignType: 'UNASSIGNED',
  currentMemberId: '',
};

/**
 * 유형자산 복제(S-214) · 일괄 변경(S-215) 처리
 */
export const useBulkActions = ({
  checkedRows,
  fetchList,
  onDone,
  canBatchUpdate = true,
}) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);

  connectorRef.current = connector;

  const [duplicateOpen, setDuplicateOpen] = useState(false);
  const [duplicateData, setDuplicateData] = useState(DUPLICATE_EMPTY);
  const [isDuplicating, setIsDuplicating] = useState(false);
  const duplicatingRef = useRef(false);

  const [batchOpen, setBatchOpen] = useState(false);
  const [batchData, setBatchData] = useState(BATCH_EMPTY);
  const [isBatchUpdating, setIsBatchUpdating] = useState(false);
  const batchUpdatingRef = useRef(false);

  const openDuplicate = useCallback(() => {
    if (!checkedRows || checkedRows.length !== 1) {
      winiMsg.showAlert('복제할 자산을 1건만 선택해주세요.');
      return;
    }
    setDuplicateData(DUPLICATE_EMPTY);
    setDuplicateOpen(true);
  }, [checkedRows]);

  const closeDuplicate = useCallback(() => setDuplicateOpen(false), []);

  const onDuplicateChange = useCallback((e) => {
    const { name, value } = e.target;
    setDuplicateData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const submitDuplicate = useCallback(async () => {
    if (duplicatingRef.current) return;
    const count = Number(duplicateData.count);
    if (!count || count < 1 || count > 100) {
      winiMsg.showAlert('복제 개수는 1~100 사이로 입력해주세요.');
      return;
    }

    duplicatingRef.current = true;
    const answer = await winiMsg.showConfirm(`${count}건 복제하시겠습니까?`);
    if (answer !== 'Y') {
      duplicatingRef.current = false;
      return;
    }

    setIsDuplicating(true);
    try {
      const currentConnector = connectorRef.current;
      if (!currentConnector) {
        winiMsg.showSnackbar('화면 연결 정보를 확인할 수 없습니다.');
        return;
      }
      const data = await duplicateTangibleAsset(
        currentConnector,
        checkedRows[0].tangibleAssetId,
        count,
      );
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('복제 되었습니다.');
        closeDuplicate();
        await fetchList();
        await onDone?.();
      } else {
        winiMsg.showSnackbar(data?.message || '복제 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('복제 중 오류가 발생했습니다.');
    } finally {
      duplicatingRef.current = false;
      setIsDuplicating(false);
    }
  }, [checkedRows, duplicateData, fetchList, closeDuplicate, onDone]);

  const openBatch = useCallback(() => {
    if (!checkedRows || checkedRows.length === 0) {
      winiMsg.showAlert('일괄 변경할 자산을 선택해주세요.');
      return;
    }
    if (!canBatchUpdate) {
      winiMsg.showAlert(
        '자산 종류·위치·사용자 옵션을 불러온 후 일괄 변경해주세요.',
      );
      return;
    }
    setBatchData(BATCH_EMPTY);
    setBatchOpen(true);
  }, [checkedRows, canBatchUpdate]);

  const closeBatch = useCallback(() => setBatchOpen(false), []);

  const onBatchChange = useCallback((e) => {
    const { name, value } = e.target;
    setBatchData((prev) => {
      const next = { ...prev, [name]: value };
      if (name === 'assignType' && !requiresMember(value)) {
        next.currentMemberId = '';
      }
      return next;
    });
  }, []);

  const onBatchToggle = useCallback(
    (name) => (e) => {
      const checked = e.target.checked;
      setBatchData((prev) => ({ ...prev, [name]: checked }));
    },
    [],
  );

  const submitBatch = useCallback(async () => {
    if (batchUpdatingRef.current) return;
    if (!canBatchUpdate) {
      winiMsg.showAlert(
        '자산 종류·위치·사용자 옵션을 불러온 후 일괄 변경해주세요.',
      );
      return;
    }
    if (
      !batchData.enableCategory &&
      !batchData.enableLocation &&
      !batchData.enableLifeStatus &&
      !batchData.enableAssignType
    ) {
      winiMsg.showAlert('변경할 항목을 하나 이상 선택해주세요.');
      return;
    }
    if (batchData.enableCategory && !batchData.categoryId) {
      winiMsg.showAlert('변경할 자산 종류를 선택해주세요.');
      return;
    }
    if (batchData.enableLocation && !batchData.locationId) {
      winiMsg.showAlert('변경할 자산 위치를 선택해주세요.');
      return;
    }
    if (
      batchData.enableAssignType &&
      requiresMember(batchData.assignType) &&
      !batchData.currentMemberId
    ) {
      winiMsg.showAlert('배정 사용자를 선택해주세요.');
      return;
    }

    batchUpdatingRef.current = true;
    const answer = await winiMsg.showConfirm(
      `선택한 ${checkedRows.length}건을 일괄 변경하시겠습니까?`,
    );
    if (answer !== 'Y') {
      batchUpdatingRef.current = false;
      return;
    }

    const payload = {
      tangibleAssetIds: checkedRows.map((row) => row.tangibleAssetId),
      ...(batchData.enableCategory ? { categoryId: batchData.categoryId } : {}),
      ...(batchData.enableLocation ? { locationId: batchData.locationId } : {}),
      ...(batchData.enableLifeStatus
        ? { lifeStatus: batchData.lifeStatus }
        : {}),
      ...(batchData.enableAssignType
        ? {
            assignType: batchData.assignType,
            currentMemberId: requiresMember(batchData.assignType)
              ? batchData.currentMemberId
              : null,
          }
        : {}),
    };

    setIsBatchUpdating(true);
    try {
      const currentConnector = connectorRef.current;
      if (!currentConnector) {
        winiMsg.showSnackbar('화면 연결 정보를 확인할 수 없습니다.');
        return;
      }
      const data = await batchModifyTangibleAsset(currentConnector, payload);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('일괄 변경 되었습니다.');
        closeBatch();
        await fetchList();
        await onDone?.();
      } else {
        winiMsg.showSnackbar(
          data?.message || '일괄 변경 중 오류가 발생했습니다.',
        );
      }
    } catch {
      winiMsg.showSnackbar('일괄 변경 중 오류가 발생했습니다.');
    } finally {
      batchUpdatingRef.current = false;
      setIsBatchUpdating(false);
    }
  }, [canBatchUpdate, checkedRows, batchData, fetchList, closeBatch, onDone]);

  return {
    duplicateOpen,
    duplicateData,
    openDuplicate,
    closeDuplicate,
    onDuplicateChange,
    submitDuplicate,
    isDuplicating,

    batchOpen,
    batchData,
    openBatch,
    closeBatch,
    onBatchChange,
    onBatchToggle,
    submitBatch,
    isBatchUpdating,
  };
};
