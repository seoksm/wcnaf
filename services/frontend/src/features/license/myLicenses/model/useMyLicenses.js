import { useCallback, useRef, useState } from 'react';
import { fetchMyLicenses, requestReleaseLicenseAssignedUser } from '@/entities/license';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/** S-550 내 라이선스 (모바일웹) - 회수 요청은 플래그만 표시한다(Phase 5 티켓 시스템 연동 전) */
export const useMyLicenses = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const formMenuIdRef = useRef(formMenuId);
  formMenuIdRef.current = formMenuId;

  const [licenses, setLicenses] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isActing, setIsActing] = useState(false);

  const load = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return;

    setIsLoading(true);
    try {
      const data = await fetchMyLicenses(currentConnector);
      if (data?.result === 'SUCCESS') {
        setLicenses(data.data || []);
      } else {
        winiMsg.showSnackbar(data?.message || '내 라이선스 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('내 라이선스 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(load, Boolean(connector));

  const requestRelease = useCallback(async (licenseAssignedUserId) => {
    const answer = await winiMsg.showConfirm('회수를 요청하시겠습니까?');
    if (answer !== 'Y') return;

    setIsActing(true);
    try {
      const data = await requestReleaseLicenseAssignedUser(connectorRef.current, licenseAssignedUserId);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('회수 요청되었습니다.');
        setLicenses((prev) => prev.map((l) => (
          l.licenseAssignedUserId === licenseAssignedUserId ? { ...l, releaseRequestedYn: true } : l
        )));
      } else {
        winiMsg.showSnackbar(data?.message || '회수 요청 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('회수 요청 중 오류가 발생했습니다.');
    } finally {
      setIsActing(false);
    }
  }, []);

  return { licenses, isLoading, isActing, requestRelease };
};
