import {
  WiniBox,
  WiniButton,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { TICKET_STATUS_LABEL } from '@/entities/ticket';

const formatDateTime = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD HH:mm') : '-');

/** S-611 내 티켓 - 코멘트로 담당자와 소통 */
export const MyTicketCommentDialog = ({
  ticket, comments, newComment, isActing, onNewCommentChange, onSubmitComment, onClose,
}) => {
  return (
    <WiniDialog open={!!ticket} onClose={onClose} fullWidth maxWidth="sm">
      <WiniDialogTitle>{ticket?.title}</WiniDialogTitle>
      <WiniDialogContent>
        {ticket && (
          <WiniBox className="flex flex-col gap-3">
            <WiniTypography variant="span" className="text-xs text-text-sub">
              상태: {TICKET_STATUS_LABEL[ticket.status]}{ticket.content ? ` · ${ticket.content}` : ''}
            </WiniTypography>
            <WiniBox className="flex flex-col gap-2">
              {(comments || []).map((c) => (
                <WiniBox key={c.ticketCommentId} className={`rounded p-2 text-xs ${c.isRequester ? 'bg-blue-50' : 'bg-gray-100'}`}>
                  <WiniBox className="flex items-center justify-between text-text-sub">
                    <span>{c.isRequester ? '나' : '담당자'}</span>
                    <span>{formatDateTime(c.createAt)}</span>
                  </WiniBox>
                  <WiniTypography variant="span" className="mt-1 block">{c.content}</WiniTypography>
                </WiniBox>
              ))}
              {(comments || []).length === 0 && (
                <WiniTypography variant="span" className="text-xs text-text-sub">등록된 코멘트가 없습니다.</WiniTypography>
              )}
            </WiniBox>
            <WiniBox className="flex items-end gap-2">
              <WiniText className="w-full" value={newComment} onChange={(e) => onNewCommentChange(e.target.value)} placeholder="코멘트 입력" multiline minRows={2} />
              <WiniButton ui="lineGray" onClick={onSubmitComment} disabled={isActing}>등록</WiniButton>
            </WiniBox>
          </WiniBox>
        )}
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="lineGray" onClick={onClose}>닫기</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
