import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { TICKET_TYPE_LABEL, TICKET_STATUS_LABEL } from '@/entities/ticket';

const STATUS_CHIP_CLASS = {
  WAITING: 'border-gray-300 text-gray-500',
  RECEIVED: 'border-blue-300 text-blue-600',
  IN_PROGRESS: 'border-orange-300 text-orange-600',
  DONE: 'border-green-300 text-green-600',
};

export const MyTicketList = ({ tickets, onOpenRegister, onSelectTicket }) => {
  return (
    <WiniBox className="flex flex-col gap-3">
      <WiniBox className="flex items-center justify-between">
        <WiniTypography variant="h2">내 티켓</WiniTypography>
        <WiniButton onClick={onOpenRegister}>새 티켓 등록</WiniButton>
      </WiniBox>

      <WiniBox className="flex flex-col gap-2">
        {(tickets || []).map((ticket) => (
          <WiniBox
            key={ticket.ticketId}
            className={`flex flex-col gap-1 rounded border border-solid bg-white p-3 ${ticket.overdue ? 'border-red-300' : 'border-gray-200'}`}
            onClick={() => onSelectTicket(ticket)}
          >
            <WiniBox className="flex items-center justify-between">
              <WiniTypography variant="span" className="text-sm font-semibold">{ticket.title}</WiniTypography>
              <span className={`rounded border border-solid px-2 py-0.5 text-xs font-semibold ${STATUS_CHIP_CLASS[ticket.status] || 'border-gray-300 text-gray-500'}`}>
                {TICKET_STATUS_LABEL[ticket.status] || ticket.status}
              </span>
            </WiniBox>
            <WiniTypography variant="span" className="text-xs text-text-sub">
              {TICKET_TYPE_LABEL[ticket.ticketType]}
              {ticket.tangibleAssetCode ? ` · ${ticket.tangibleAssetCode}` : ''}
            </WiniTypography>
            {ticket.targetDueDate && (
              <WiniTypography variant="span" className={`text-xs font-semibold ${ticket.overdue ? 'text-red-600' : 'text-text-sub'}`}>
                목표일 {winiDate.dateFormat(winiDate(ticket.targetDueDate), 'YYYY-MM-DD')}{ticket.overdue ? ' - 기한초과' : ''}
              </WiniTypography>
            )}
          </WiniBox>
        ))}
        {(tickets || []).length === 0 && (
          <WiniBox ui="info" className="p-4 text-center">
            <WiniTypography variant="span" className="text-text-sub">등록된 티켓이 없습니다.</WiniTypography>
          </WiniBox>
        )}
      </WiniBox>
    </WiniBox>
  );
};
