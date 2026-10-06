import { useCallback, useRef, useState } from 'react';
import {
  fetchTicket, assignTicket, changeTicketStatus, completeTicket,
  fetchTicketComments, addTicketComment,
} from '@/entities/ticket';
import { fetchCommonUsers } from '@/entities/tangibleAsset';
import { fetchAssetCategories } from '@/entities/assetCategory';
import { fetchAssetLocations } from '@/entities/assetLocation';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_COMPLETE_FORM = {
  assetName: '', categoryId: '', locationId: '', acquisitionDate: '', acquisitionAmount: '',
  modelName: '', manufacturer: '', serialNo: '', memo: '', requesterName: '', managerName: '',
};

/** S-602 티켓 상세 - 코멘트, 배정, 상태 변경, 완료 처리(PURCHASE는 자산 등록 폼 포함) */
export const useTicketDetail = ({ onChanged }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [selectedTicket, setSelectedTicket] = useState(null);
  const [comments, setComments] = useState([]);
  const [userList, setUserList] = useState([]);
  const [categoryList, setCategoryList] = useState([]);
  const [locationList, setLocationList] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isActing, setIsActing] = useState(false);
  const actingRef = useRef(false);
  const [newComment, setNewComment] = useState('');
  const [assigneeDraft, setAssigneeDraft] = useState('');
  const [completeForm, setCompleteForm] = useState(EMPTY_COMPLETE_FORM);

  const userNameById = useCallback((memberId) => {
    const user = userList.find((u) => u.id === memberId);
    return user ? (user.fullName || user.username) : memberId;
  }, [userList]);

  const loadComments = useCallback(async (ticketId) => {
    const data = await fetchTicketComments(connectorRef.current, ticketId);
    if (data?.result === 'SUCCESS') setComments(data.data || []);
  }, []);

  const openDetail = useCallback(async (ticketRow) => {
    const currentConnector = connectorRef.current;
    setNewComment('');
    setCompleteForm(EMPTY_COMPLETE_FORM);
    setIsLoading(true);
    try {
      const data = await fetchTicket(currentConnector, ticketRow.ticketId);
      const ticket = data?.result === 'SUCCESS' ? data.data : ticketRow;
      setSelectedTicket(ticket);
      setAssigneeDraft(ticket.assigneeId || '');
      await loadComments(ticket.ticketId);

      const [usersRes, categoriesRes, locationsRes] = await Promise.all([
        fetchCommonUsers(currentConnector), fetchAssetCategories(currentConnector), fetchAssetLocations(currentConnector),
      ]);
      if (usersRes?.result === 'SUCCESS') setUserList(usersRes.data || []);
      if (categoriesRes?.result === 'SUCCESS') setCategoryList(categoriesRes.data || []);
      if (locationsRes?.result === 'SUCCESS') setLocationList(locationsRes.data || []);
    } catch {
      winiMsg.showSnackbar('티켓 상세 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, [loadComments]);

  const closeDetail = useCallback(() => setSelectedTicket(null), []);

  const handleAssigneeChange = useCallback((e) => setAssigneeDraft(e.target.value), []);

  const submitAssign = useCallback(async () => {
    if (actingRef.current) return;
    if (!assigneeDraft) {
      winiMsg.showAlert('담당자를 선택해주세요.');
      return;
    }
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await assignTicket(connectorRef.current, selectedTicket.ticketId, assigneeDraft);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('배정되었습니다.');
        setSelectedTicket((prev) => ({ ...prev, assigneeId: assigneeDraft, unassigned: false }));
        await onChanged?.();
      } else {
        winiMsg.showSnackbar(data?.message || '배정 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('배정 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedTicket, assigneeDraft, onChanged]);

  const changeStatus = useCallback(async (newStatus) => {
    if (actingRef.current) return;
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await changeTicketStatus(connectorRef.current, selectedTicket.ticketId, newStatus);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('상태가 변경되었습니다.');
        setSelectedTicket((prev) => ({ ...prev, status: newStatus }));
        await onChanged?.();
      } else {
        winiMsg.showSnackbar(data?.message || '상태 변경 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('상태 변경 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedTicket, onChanged]);

  const handleCompleteFormChange = useCallback((e) => {
    const { name, value } = e.target;
    setCompleteForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  const submitComplete = useCallback(async () => {
    if (actingRef.current) return;
    const isPurchase = selectedTicket?.ticketType === 'PURCHASE';
    if (isPurchase) {
      if (!completeForm.assetName || !completeForm.categoryId || !completeForm.locationId
          || !completeForm.acquisitionDate || !completeForm.acquisitionAmount) {
        winiMsg.showAlert('자산명·종류·위치·취득일·취득가액은 필수입니다.');
        return;
      }
      if (!completeForm.requesterName) {
        winiMsg.showAlert('요청자명을 입력해주세요(수령확인서에 필요).');
        return;
      }
    }

    const answer = await winiMsg.showConfirm('완료 처리하시겠습니까? 완료 후에는 수정할 수 없습니다.');
    if (answer !== 'Y') return;
    if (actingRef.current) return;

    const body = isPurchase ? {
      assetName: completeForm.assetName,
      categoryId: completeForm.categoryId,
      locationId: completeForm.locationId,
      acquisitionDate: completeForm.acquisitionDate,
      acquisitionAmount: Number(completeForm.acquisitionAmount),
      modelName: completeForm.modelName || undefined,
      manufacturer: completeForm.manufacturer || undefined,
      serialNo: completeForm.serialNo || undefined,
      memo: completeForm.memo || undefined,
      requesterName: completeForm.requesterName,
      managerName: completeForm.managerName || undefined,
    } : undefined;

    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await completeTicket(connectorRef.current, selectedTicket.ticketId, body);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('완료 처리되었습니다.');
        setSelectedTicket((prev) => ({ ...prev, status: 'DONE' }));
        await onChanged?.();
      } else {
        winiMsg.showSnackbar(data?.message || '완료 처리 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('완료 처리 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedTicket, completeForm, onChanged]);

  const submitComment = useCallback(async () => {
    if (actingRef.current) return;
    if (!newComment.trim()) return;
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await addTicketComment(connectorRef.current, selectedTicket.ticketId, newComment.trim());
      if (data?.result === 'SUCCESS') {
        setNewComment('');
        await loadComments(selectedTicket.ticketId);
      } else {
        winiMsg.showSnackbar(data?.message || '코멘트 등록 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('코멘트 등록 중 오류가 발생했습니다.');
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, [selectedTicket, newComment, loadComments]);

  return {
    selectedTicket, comments, userList, categoryList, locationList,
    isLoading, isActing, newComment, assigneeDraft, completeForm,
    userNameById, openDetail, closeDetail,
    handleAssigneeChange, submitAssign, changeStatus,
    handleCompleteFormChange, submitComplete,
    setNewComment, submitComment,
  };
};
