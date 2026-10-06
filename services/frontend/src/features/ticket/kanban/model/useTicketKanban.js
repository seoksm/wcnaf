import { useCallback, useMemo, useRef, useState } from 'react';
import { fetchTicketKanban, changeTicketStatus, TICKET_STATUS_ORDER } from '@/entities/ticket';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/** S-600 칸반 보드 - 데스크톱 드래그, DONE은 완료 처리 API로만 진입(드래그 대상 아님) */
export const useTicketKanban = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const [tickets, setTickets] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isActing, setIsActing] = useState(false);
  const actingRef = useRef(false);

  const load = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return;

    setIsLoading(true);
    try {
      const data = await fetchTicketKanban(currentConnector);
      if (data?.result === 'SUCCESS') {
        setTickets(data.data || []);
      } else {
        winiMsg.showSnackbar(data?.message || '칸반 보드 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('칸반 보드 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(load, Boolean(connector));

  const columns = useMemo(() => {
    const grouped = TICKET_STATUS_ORDER.reduce((acc, status) => ({ ...acc, [status]: [] }), {});
    tickets.forEach((ticket) => {
      (grouped[ticket.status] ||= []).push(ticket);
    });
    return grouped;
  }, [tickets]);

  /** 드래그로는 WAITING·RECEIVED·IN_PROGRESS 사이에서만 이동한다 - DONE은 완료 처리 API 전용.
   * actingRef로 드롭을 빠르게 반복해도 상태 변경 API가 한 번만 호출되게 막는다. */
  const moveTicket = useCallback(async (ticketId, fromStatus, toStatus) => {
    if (actingRef.current) return;
    if (fromStatus === toStatus) return;
    if (fromStatus === 'DONE' || toStatus === 'DONE') {
      winiMsg.showAlert('완료 처리는 상세 화면의 "완료 처리" 버튼으로만 할 수 있습니다.');
      return;
    }
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await changeTicketStatus(connectorRef.current, ticketId, toStatus);
      if (data?.result === 'SUCCESS') {
        await load();
      } else {
        winiMsg.showSnackbar(data?.message || '상태 변경 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('상태 변경 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [load]);

  return { columns, isLoading, isActing, moveTicket, reload: load };
};
