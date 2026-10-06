import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { TICKET_TYPE_LABEL, TICKET_STATUS_LABEL } from '@/entities/ticket';

const formatDate = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD') : '');

const columnDefs = [
  {
    field: 'ticketType', headerName: '유형', flex: 0.8, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => TICKET_TYPE_LABEL[params.value] || params.value,
  },
  { field: 'title', headerName: '제목', flex: 1.6, headerClass: 'ag-header-center', cellClass: 'text-center' },
  {
    field: 'status', headerName: '상태', flex: 0.8, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => TICKET_STATUS_LABEL[params.value] || params.value,
  },
  {
    field: 'unassigned', headerName: '담당자', flex: 0.8, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => (params.value ? '미배정' : '배정됨'),
  },
  { field: 'targetDueDate', headerName: '목표일', flex: 0.9, headerClass: 'ag-header-center', cellClass: 'text-center', valueFormatter: formatDate },
  {
    field: 'overdue', headerName: '기한', flex: 0.7, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => (params.value ? '초과' : '-'),
  },
];

export const Grid = ({ rowData, isLoading, onRowSelect }) => {
  return (
    <WiniBox className="h-[560px]">
      <WiniAgGridReact
        rowData={rowData || []}
        columnDefs={columnDefs}
        getRowId={(params) => params.data.ticketId}
        onRowClicked={(e) => onRowSelect?.(e?.data)}
        loading={isLoading}
      />
    </WiniBox>
  );
};
