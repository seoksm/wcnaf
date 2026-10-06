import { useState } from 'react';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { TICKET_STATUS_LABEL, TICKET_STATUS_ORDER } from '@/entities/ticket';
import { KanbanCard } from './KanbanCard';

/**
 * S-600 칸반 보드 - 네이티브 HTML5 드래그&드롭(별도 라이브러리 불필요). DONE 컬럼은 완료된
 * 티켓만 보여주고(이번 주 것만, 서버가 이미 필터링) 드롭 대상이 아니다.
 */
export const KanbanBoard = ({ columns, isActing, onMoveTicket, onCardClick }) => {
  const [draggingId, setDraggingId] = useState(null);
  const [dragOverStatus, setDragOverStatus] = useState(null);

  return (
    <WiniBox className="flex gap-3 overflow-x-auto pb-2">
      {TICKET_STATUS_ORDER.map((status) => {
        const tickets = columns[status] || [];
        const droppable = status !== 'DONE';

        return (
          <WiniBox
            key={status}
            ui="noAutoGap"
            className={`w-64 flex-shrink-0 rounded p-2 ${dragOverStatus === status && droppable ? 'bg-blue-50' : 'bg-gray-50'}`}
            onDragOver={(e) => {
              if (!droppable) return;
              e.preventDefault();
              setDragOverStatus(status);
            }}
            onDragLeave={() => setDragOverStatus((prev) => (prev === status ? null : prev))}
            onDrop={(e) => {
              e.preventDefault();
              setDragOverStatus(null);
              if (!droppable || !draggingId) return;
              const { ticketId, fromStatus } = JSON.parse(draggingId);
              onMoveTicket(ticketId, fromStatus, status);
            }}
          >
            <WiniTypography variant="span" className="mb-2 flex items-center justify-between text-sm font-semibold">
              {TICKET_STATUS_LABEL[status]}
              <span className="text-text-sub">{tickets.length}</span>
            </WiniTypography>
            <WiniBox className="flex flex-col gap-2">
              {tickets.map((ticket) => (
                <KanbanCard
                  key={ticket.ticketId}
                  ticket={ticket}
                  draggable={status !== 'DONE' && !isActing}
                  onDragStart={() => setDraggingId(JSON.stringify({ ticketId: ticket.ticketId, fromStatus: status }))}
                  onClick={() => onCardClick(ticket)}
                />
              ))}
              {tickets.length === 0 && (
                <WiniTypography variant="span" className="text-xs text-text-sub">없음</WiniTypography>
              )}
            </WiniBox>
          </WiniBox>
        );
      })}
    </WiniBox>
  );
};
