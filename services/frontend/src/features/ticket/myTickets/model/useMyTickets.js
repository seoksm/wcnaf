import { useCallback, useRef, useState } from 'react';
import {
  fetchMyTickets, createTicketBySelf, fetchTicketComments, addTicketComment,
} from '@/entities/ticket';
import { fetchTangibleAssets } from '@/entities/tangibleAsset';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

const EMPTY_FORM = {
  ticketType: 'REPAIR', title: '', content: '',
  assetCode: '', tangibleAssetId: '', assetName: '',
};

/** S-610 티켓 등록(임직원) · S-611 내 티켓(코멘트) */
export const useMyTickets = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const [tickets, setTickets] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isActing, setIsActing] = useState(false);
  const actingRef = useRef(false);

  const [registerOpen, setRegisterOpen] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);
  const [isResolving, setIsResolving] = useState(false);

  const [selectedTicket, setSelectedTicket] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');

  const load = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return;

    setIsLoading(true);
    try {
      const data = await fetchMyTickets(currentConnector);
      if (data?.result === 'SUCCESS') {
        setTickets(data.data || []);
      } else {
        winiMsg.showSnackbar(data?.message || '내 티켓 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('내 티켓 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(load, Boolean(connector));

  const openRegister = useCallback(() => {
    setForm(EMPTY_FORM);
    setRegisterOpen(true);
  }, []);

  const closeRegister = useCallback(() => setRegisterOpen(false), []);

  const handleFormChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value, ...(name === 'assetCode' ? { tangibleAssetId: '', assetName: '' } : {}) }));
  }, []);

  /** S-610 "QR 간편 신청" 대신 자산코드 직접 조회로 단순화(S-412 관리자 대행과 동일한 판단) */
  const resolveAsset = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !form.assetCode?.trim()) return;
    setIsResolving(true);
    try {
      const data = await fetchTangibleAssets(currentConnector, { keyword: form.assetCode.trim(), page: 0, size: 5 });
      const matched = (data?.data?.content || []).find((a) => a.assetCode === form.assetCode.trim());
      if (matched) {
        setForm((prev) => ({ ...prev, tangibleAssetId: matched.tangibleAssetId, assetName: matched.assetName }));
      } else {
        setForm((prev) => ({ ...prev, tangibleAssetId: '', assetName: '' }));
        winiMsg.showSnackbar('자산코드를 찾을 수 없습니다.');
      }
    } catch {
      winiMsg.showSnackbar('자산 조회 중 오류가 발생했습니다.');
    } finally {
      setIsResolving(false);
    }
  }, [form.assetCode]);

  const submitRegister = useCallback(async () => {
    if (actingRef.current) return;
    if (!form.title.trim()) {
      winiMsg.showAlert('제목은 필수입니다.');
      return;
    }
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await createTicketBySelf(connectorRef.current, {
        ticketType: form.ticketType,
        title: form.title,
        content: form.content || undefined,
        tangibleAssetId: form.tangibleAssetId || undefined,
      });
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('등록되었습니다.');
        setRegisterOpen(false);
        await load();
      } else {
        winiMsg.showSnackbar(data?.message || '등록 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('등록 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [form, load]);

  const openTicket = useCallback(async (ticket) => {
    setSelectedTicket(ticket);
    setNewComment('');
    const data = await fetchTicketComments(connectorRef.current, ticket.ticketId);
    if (data?.result === 'SUCCESS') setComments(data.data || []);
  }, []);

  const closeTicket = useCallback(() => setSelectedTicket(null), []);

  const submitComment = useCallback(async () => {
    if (actingRef.current) return;
    if (!newComment.trim()) return;
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await addTicketComment(connectorRef.current, selectedTicket.ticketId, newComment.trim());
      if (data?.result === 'SUCCESS') {
        setNewComment('');
        const commentsRes = await fetchTicketComments(connectorRef.current, selectedTicket.ticketId);
        if (commentsRes?.result === 'SUCCESS') setComments(commentsRes.data || []);
      } else {
        winiMsg.showSnackbar(data?.message || '코멘트 등록 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('코멘트 등록 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedTicket, newComment]);

  return {
    tickets, isLoading, isActing,
    registerOpen, form, isResolving, openRegister, closeRegister, handleFormChange, resolveAsset, submitRegister,
    selectedTicket, comments, newComment, openTicket, closeTicket, setNewComment, submitComment,
  };
};
