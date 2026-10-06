import { useCallback, useRef, useState } from 'react';
import { fetchInventorySchedules, fetchInventoryCloneTemplate } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

/**
 * S-306 반복 시행 스케줄 - §4 "자동 시행은 직전 조사 종료를 전제로 한다(I2)"의 자동 실행(cron)은
 * 이번 단계에서 만들지 않았다(알림 인프라가 없어 "실행 건너뜀" 안내를 보낼 방법이 없고, 사람 확인
 * 없이 org-wide 자산을 쓸어담는 조사를 무인으로 새로 만드는 것도 성급하다). 대신 예정일 안내 +
 * 직전 조사 설정 그대로 복제해 새로 만드는 수동 동선("템플릿 복제")까지만 지원한다.
 */
export const useInventorySchedules = ({ onCloneMember, onCloneAdmin }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [open, setOpen] = useState(false);
  const [schedules, setSchedules] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isCloning, setIsCloning] = useState(false);

  const load = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await fetchInventorySchedules(currentConnector);
      if (data?.result === 'SUCCESS') {
        setSchedules(Array.isArray(data.data) ? data.data : []);
      } else {
        const message = data?.message || '반복 시행 스케줄 조회에 실패했습니다.';
        setError(message);
        winiMsg.showSnackbar(message);
      }
    } catch {
      const message = '반복 시행 스케줄 조회 중 오류가 발생했습니다.';
      setError(message);
      winiMsg.showSnackbar(message);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const togglePanel = useCallback(async () => {
    if (open) {
      setOpen(false);
      return;
    }
    setOpen(true);
    await load();
  }, [open, load]);

  const cloneFrom = useCallback(async (schedule) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || isCloning) return;
    setIsCloning(true);
    try {
      const data = await fetchInventoryCloneTemplate(currentConnector, schedule.lastClosedInventoryId);
      if (data?.result !== 'SUCCESS') {
        winiMsg.showSnackbar(data?.message || '템플릿 조회 중 오류가 발생했습니다.');
        return;
      }
      const t = data.data;
      if (t.inventoryType === 'MEMBER') {
        onCloneMember?.({
          title: t.title,
          approvalRequired: t.approvalRequired,
          allowNewAssetRegistration: t.allowNewAssetRegistration,
          recurrenceRule: t.recurrenceRule || '',
          excludedMemberIds: t.excludedMemberIds || [],
        });
      } else {
        onCloneAdmin?.({
          title: t.title,
          approvalRequired: t.approvalRequired,
          allowNewAssetRegistration: t.allowNewAssetRegistration,
          recurrenceRule: t.recurrenceRule || '',
          inspectorMemberIds: t.inspectorMemberIds || [],
        });
      }
    } catch {
      winiMsg.showSnackbar('템플릿 조회 중 오류가 발생했습니다.');
    } finally {
      setIsCloning(false);
    }
  }, [isCloning, onCloneMember, onCloneAdmin]);

  return { open, schedules, isLoading, error, isCloning, togglePanel, cloneFrom };
};
