import { useCallback, useRef, useState } from 'react';
import { fetchMyAcknowledgements, fetchMyAcknowledgementDetail, approveAcknowledgementBySelf } from '@/entities/acknowledgement';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/** S-440 확인서 승인 (임직원) - K4: 체크박스 동의 + 승인 버튼 2단계 확인 */
export const useMyAcknowledgements = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const [list, setList] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [mode, setMode] = useState('list');
  const [detail, setDetail] = useState(null);
  const [isDetailLoading, setIsDetailLoading] = useState(false);
  const [agreed, setAgreed] = useState(false);
  const [isApproving, setIsApproving] = useState(false);
  const approvingRef = useRef(false);

  const load = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return;

    setIsLoading(true);
    try {
      const data = await fetchMyAcknowledgements(currentConnector);
      if (data?.result === 'SUCCESS') {
        setList(data.data || []);
      } else {
        winiMsg.showSnackbar(data?.message || '내 확인서 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('내 확인서 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(load, Boolean(connector));

  const openDetail = useCallback(async (row) => {
    setMode('detail');
    setAgreed(false);
    setIsDetailLoading(true);
    try {
      const data = await fetchMyAcknowledgementDetail(connectorRef.current, row.acknowledgementId);
      if (data?.result === 'SUCCESS') {
        setDetail(data.data);
      } else {
        winiMsg.showSnackbar(data?.message || '확인서 상세 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('확인서 상세 조회 중 오류가 발생했습니다.');
    } finally {
      setIsDetailLoading(false);
    }
  }, []);

  const backToList = useCallback(() => {
    setMode('list');
    setDetail(null);
    load();
  }, [load]);

  const approve = useCallback(async () => {
    if (approvingRef.current || !agreed || !detail) return;
    approvingRef.current = true;
    setIsApproving(true);
    try {
      const data = await approveAcknowledgementBySelf(connectorRef.current, detail.acknowledgementId);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('승인되었습니다.');
        backToList();
      } else {
        winiMsg.showSnackbar(data?.message || '승인 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('승인 중 오류가 발생했습니다.');
    } finally {
      approvingRef.current = false;
      setIsApproving(false);
    }
  }, [detail, agreed, backToList]);

  return { list, isLoading, mode, detail, isDetailLoading, agreed, setAgreed, isApproving, openDetail, backToList, approve };
};
