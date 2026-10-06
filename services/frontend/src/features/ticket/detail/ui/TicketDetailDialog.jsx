import {
  WiniBox,
  WiniButton,
  WiniDatePicker,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniGridItem,
  WiniGridLayout,
  WiniMenuItem,
  WiniSelect,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { TICKET_TYPE_LABEL, TICKET_STATUS_LABEL } from '@/entities/ticket';

const formatDateTime = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD HH:mm') : '-');
const NON_DONE_STATUSES = ['WAITING', 'RECEIVED', 'IN_PROGRESS'];

/**
 * S-602 티켓 상세 · 코멘트 - 완료 처리 시 PURCHASE 유형만 자산 등록 폼이 함께 열린다(Q-49
 * 연쇄 처리: 자산 생성 → 요청자 배정 → 수령확인서 요청). 완료 후에는 모든 조작이 잠긴다.
 */
export const TicketDetailDialog = ({
  ticket, comments, userList, categoryList, locationList,
  isLoading, isActing, newComment, assigneeDraft, completeForm,
  userNameById, onAssigneeChange, onSubmitAssign, onChangeStatus,
  onCompleteFormChange, onSubmitComplete,
  onNewCommentChange, onSubmitComment, onClose,
}) => {
  if (!ticket) {
    return <WiniDialog open={false} onClose={onClose} />;
  }

  const isDone = ticket.status === 'DONE';
  const isPurchase = ticket.ticketType === 'PURCHASE';

  return (
    <WiniDialog open={!!ticket} onClose={onClose} fullWidth maxWidth="md">
      <WiniDialogTitle>티켓 상세</WiniDialogTitle>
      <WiniDialogContent>
        <WiniBox className="flex flex-col gap-4">
          <WiniBox className="flex items-center justify-between">
            <WiniBox>
              <WiniTypography variant="span" className="text-sm font-semibold">{ticket.title}</WiniTypography>
              <WiniTypography variant="span" className="block text-xs text-text-sub">
                {TICKET_TYPE_LABEL[ticket.ticketType]}
                {ticket.tangibleAssetCode ? ` · 연결자산 ${ticket.tangibleAssetCode}(${ticket.tangibleAssetName})` : ''}
                {ticket.content ? ` · ${ticket.content}` : ''}
              </WiniTypography>
            </WiniBox>
            <span className={`rounded border border-solid px-2 py-0.5 text-xs font-semibold ${ticket.overdue ? 'border-red-300 text-red-600' : 'border-gray-300 text-text-sub'}`}>
              {TICKET_STATUS_LABEL[ticket.status]}{ticket.overdue ? ' · 기한초과' : ''}
            </span>
          </WiniBox>

          {!isDone && (
            <WiniGridLayout container columnSpacing={2} rowSpacing={1} className="items-end">
              <WiniGridItem size={{ xs: 4 }}>
                <WiniSelect ui="column" label="담당자" className="w-full" value={assigneeDraft} displayEmpty onChange={onAssigneeChange}>
                  <WiniMenuItem value="">(미배정)</WiniMenuItem>
                  {userList.map((u) => (
                    <WiniMenuItem value={u.id} key={u.id}>{u.fullName || u.username}</WiniMenuItem>
                  ))}
                </WiniSelect>
              </WiniGridItem>
              <WiniGridItem size={{ xs: 2 }}>
                <WiniButton ui="lineGray" className="w-full" onClick={onSubmitAssign} disabled={isActing}>배정</WiniButton>
              </WiniGridItem>
              <WiniGridItem size={{ xs: 6 }}>
                <WiniBox className="flex justify-end gap-2">
                  {NON_DONE_STATUSES.map((status) => (
                    <WiniButton
                      key={status}
                      ui={status === ticket.status ? 'line' : 'lineGray'}
                      onClick={() => onChangeStatus(status)}
                      disabled={isActing || status === ticket.status}
                    >
                      {TICKET_STATUS_LABEL[status]}
                    </WiniButton>
                  ))}
                </WiniBox>
              </WiniGridItem>
            </WiniGridLayout>
          )}

          {!isDone && (
            <WiniBox className="rounded border border-solid border-gray-200 p-3">
              <WiniTypography variant="span" className="mb-2 block text-sm font-semibold">완료 처리</WiniTypography>
              {isPurchase ? (
                <WiniBox className="flex flex-col gap-2">
                  <WiniTypography variant="span" className="text-xs text-text-sub">
                    신규구매 완료 시 자산이 자동 등록되고 요청자에게 배정된 뒤 수령확인서가 요청됩니다(S-212 필수 5개와 동일).
                  </WiniTypography>
                  <WiniGridLayout container columnSpacing={2} rowSpacing={1}>
                    <WiniGridItem size={{ xs: 6 }}>
                      <WiniText ui="column" label="자산명" name="assetName" required className="w-full" value={completeForm.assetName} onChange={onCompleteFormChange} slotProps={{ inputLabel: { shrink: true } }} />
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 6 }}>
                      <WiniSelect ui="column" label="자산 종류" name="categoryId" className="w-full" value={completeForm.categoryId} displayEmpty onChange={onCompleteFormChange}>
                        <WiniMenuItem value="">(선택)</WiniMenuItem>
                        {categoryList.map((c) => (
                          <WiniMenuItem value={c.categoryId} key={c.categoryId}>{c.categoryName}</WiniMenuItem>
                        ))}
                      </WiniSelect>
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 6 }}>
                      <WiniSelect ui="column" label="설치 위치" name="locationId" className="w-full" value={completeForm.locationId} displayEmpty onChange={onCompleteFormChange}>
                        <WiniMenuItem value="">(선택)</WiniMenuItem>
                        {locationList.map((l) => (
                          <WiniMenuItem value={l.locationId} key={l.locationId}>{l.locationName}</WiniMenuItem>
                        ))}
                      </WiniSelect>
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 3 }}>
                      <WiniDatePicker ui="column" label="취득일" className="w-full" value={completeForm.acquisitionDate || ''} onChange={(e) => onCompleteFormChange({ target: { name: 'acquisitionDate', value: e?.value ? winiDate.dateFormat(e.value, 'YYYY-MM-DD') : '' } })} slotProps={{ inputLabel: { shrink: true } }} />
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 3 }}>
                      <WiniText ui="column" label="취득가액" name="acquisitionAmount" required className="w-full" value={completeForm.acquisitionAmount} onChange={onCompleteFormChange} slotProps={{ inputLabel: { shrink: true } }} />
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 6 }}>
                      <WiniText ui="column" label="요청자명(확인서용)" name="requesterName" required className="w-full" value={completeForm.requesterName} onChange={onCompleteFormChange} slotProps={{ inputLabel: { shrink: true } }} />
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 6 }}>
                      <WiniText ui="column" label="담당자명(확인서용)" name="managerName" className="w-full" value={completeForm.managerName} onChange={onCompleteFormChange} slotProps={{ inputLabel: { shrink: true } }} />
                    </WiniGridItem>
                  </WiniGridLayout>
                </WiniBox>
              ) : (
                <WiniTypography variant="span" className="text-xs text-text-sub">
                  {ticket.ticketType === 'RETURN' ? '완료 처리하면 연결된 라이선스 배정이 회수됩니다.' : '완료 처리하면 상태가 완료로 바뀌고 더 이상 수정할 수 없습니다.'}
                </WiniTypography>
              )}
              <WiniBox className="mt-2 flex justify-end">
                <WiniButton ui="line" onClick={onSubmitComplete} disabled={isActing}>완료 처리</WiniButton>
              </WiniBox>
            </WiniBox>
          )}

          <WiniBox>
            <WiniTypography variant="span" className="mb-2 block text-sm font-semibold">코멘트</WiniTypography>
            {isLoading ? (
              <WiniTypography variant="span" className="text-xs text-text-sub">불러오는 중...</WiniTypography>
            ) : (
              <WiniBox className="flex flex-col gap-2">
                {comments.map((c) => (
                  <WiniBox key={c.ticketCommentId} className={`rounded p-2 text-xs ${c.isRequester ? 'bg-blue-50' : 'bg-gray-100'}`}>
                    <WiniBox className="flex items-center justify-between">
                      <span className="font-semibold">{userNameById(c.writtenBy)}{c.isRequester ? ' (요청자)' : ''}</span>
                      <span className="text-text-sub">{formatDateTime(c.createAt)}</span>
                    </WiniBox>
                    <WiniTypography variant="span" className="mt-1 block">{c.content}</WiniTypography>
                  </WiniBox>
                ))}
                {comments.length === 0 && (
                  <WiniTypography variant="span" className="text-xs text-text-sub">등록된 코멘트가 없습니다.</WiniTypography>
                )}
              </WiniBox>
            )}
            <WiniBox className="mt-2 flex items-end gap-2">
              <WiniText className="w-full" value={newComment} onChange={(e) => onNewCommentChange(e.target.value)} placeholder="코멘트 입력" multiline minRows={2} />
              <WiniButton ui="lineGray" onClick={onSubmitComment} disabled={isActing}>등록</WiniButton>
            </WiniBox>
          </WiniBox>
        </WiniBox>
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="lineGray" onClick={onClose}>닫기</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
