import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { TICKET_TYPE_LABEL } from '@/entities/ticket';

const formatDate = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD') : '-');

/** 자산 있는 티켓(수리 등)과 없는 티켓(신규구매)이 한눈에 구분되도록 자산코드를 칩으로 표시 */
export const KanbanCard = ({ ticket, draggable, onDragStart, onClick }) => {
  return (
    <WiniBox
      className={`cursor-pointer rounded border border-solid bg-white p-2 text-xs ${ticket.overdue ? 'border-red-300' : 'border-gray-200'}`}
      draggable={draggable}
      onDragStart={draggable ? onDragStart : undefined}
      onClick={onClick}
    >
      <WiniBox className="flex items-center justify-between">
        <span className="rounded border border-solid border-gray-300 px-1.5 py-0.5 text-[11px]">{TICKET_TYPE_LABEL[ticket.ticketType]}</span>
        {ticket.tangibleAssetCode && (
          <span className="rounded border border-solid border-blue-300 px-1.5 py-0.5 text-[11px] text-blue-600">{ticket.tangibleAssetCode}</span>
        )}
      </WiniBox>
      <WiniTypography variant="span" className="mt-1 block font-semibold">{ticket.title}</WiniTypography>
      <WiniBox className="mt-1 flex items-center justify-between text-[11px] text-text-sub">
        <span>목표일 {formatDate(ticket.targetDueDate)}</span>
        {ticket.unassigned && <span className="font-semibold text-orange-600">미배정</span>}
        {ticket.overdue && <span className="font-semibold text-red-600">기한초과</span>}
      </WiniBox>
    </WiniBox>
  );
};
