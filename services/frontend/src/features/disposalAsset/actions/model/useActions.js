import { useCallback, useRef, useState } from 'react';
import { restoreTangibleAsset, disposeTangibleAsset } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const DISPOSE_EMPTY = {
  disposalReasonCode: '',
  disposalAmount: '',
  counterparty: '',
  memo: '',
};

/**
 * 불용자산 목록에서 선택한 자산에 대한 복귀(S-240, Q-28)와 처분 처리(S-242, Q-26) - 둘 다
 * 현재 선택된 행 하나를 대상으로 하는 동작이라 하나의 훅으로 묶는다.
 */
export const useDisposalActions = ({ fetchList, onDone }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [isRestoring, setIsRestoring] = useState(false);
  const restoringRef = useRef(false);

  const [disposeData, setDisposeData] = useState(DISPOSE_EMPTY);
  const [isDisposing, setIsDisposing] = useState(false);
  const disposingRef = useRef(false);

  const resetDisposeForm = useCallback(() => setDisposeData(DISPOSE_EMPTY), []);

  const onDisposeChange = useCallback((e) => {
    const { name, value } = e.target;
    setDisposeData((prev) => ({ ...prev, [name]: value }));
  }, []);

  const restore = useCallback(
    async (tangibleAssetId) => {
      const currentConnector = connectorRef.current;
      if (
        !currentConnector ||
        !tangibleAssetId ||
        restoringRef.current ||
        disposingRef.current
      )
        return;

      restoringRef.current = true;
      setIsRestoring(true);
      try {
        const answer = await winiMsg.showConfirm(
          '선택한 자산을 사용 상태로 복귀하시겠습니까?',
        );
        if (answer !== 'Y') return;

        const data = await restoreTangibleAsset(currentConnector, tangibleAssetId);
        if (data?.result === 'SUCCESS') {
          winiMsg.showSnackbar('사용 상태로 복귀되었습니다.');
          resetDisposeForm();
          // onDone에는 새로 조회한 목록을 그대로 전달한다 - onDone이 선택된 행을 다시 찾아 갱신하기 위해
          // fetchList를 또 호출하면 같은 데이터를 두 번 불러오게 되므로, 여기서 한 번만 호출해 재사용한다.
          const freshList = await fetchList?.();
          await onDone?.(freshList);
        } else {
          winiMsg.showSnackbar(data?.message || '복귀 중 오류가 발생했습니다.');
        }
      } catch {
        winiMsg.showSnackbar('복귀 중 오류가 발생했습니다.');
      } finally {
        restoringRef.current = false;
        setIsRestoring(false);
      }
    },
    [fetchList, onDone, resetDisposeForm],
  );

  const dispose = useCallback(
    async (tangibleAssetId) => {
      const currentConnector = connectorRef.current;
      if (
        !currentConnector ||
        !tangibleAssetId ||
        disposingRef.current ||
        restoringRef.current
      )
        return;
      if (!disposeData.disposalReasonCode) {
        winiMsg.showAlert('처분 사유 은/는 필수입력값입니다.');
        return;
      }

      disposingRef.current = true;
      setIsDisposing(true);
      try {
        const answer = await winiMsg.showConfirm(
          '처분 처리하시겠습니까?\n처분 처리는 되돌릴 수 없습니다.',
        );
        if (answer !== 'Y') return;

        const data = await disposeTangibleAsset(currentConnector, tangibleAssetId, {
          disposalReasonCode: disposeData.disposalReasonCode,
          disposalAmount:
            disposeData.disposalAmount === ''
              ? null
              : Number(disposeData.disposalAmount),
          counterparty: disposeData.counterparty || null,
          memo: disposeData.memo || null,
        });
        if (data?.result === 'SUCCESS') {
          winiMsg.showSnackbar('처분 처리 되었습니다.');
          resetDisposeForm();
          const freshList = await fetchList?.();
          await onDone?.(freshList);
        } else {
          winiMsg.showSnackbar(
            data?.message || '처분 처리 중 오류가 발생했습니다.',
          );
        }
      } catch {
        winiMsg.showSnackbar('처분 처리 중 오류가 발생했습니다.');
      } finally {
        disposingRef.current = false;
        setIsDisposing(false);
      }
    },
    [disposeData, fetchList, onDone, resetDisposeForm],
  );

  return {
    isRestoring,
    restore,
    disposeData,
    onDisposeChange,
    resetDisposeForm,
    isDisposing,
    dispose,
  };
};
