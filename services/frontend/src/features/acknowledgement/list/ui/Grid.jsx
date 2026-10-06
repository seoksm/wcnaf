import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { ACK_STATUS_LABEL } from '@/entities/acknowledgement';
import { ACK_TYPE_LABEL } from '@/entities/ackTemplate';

const formatDate = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD') : '');
const formatDateTime = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD HH:mm') : '');

const buildColumnDefs = (memberNameById) => [
  { field: 'assetCode', headerName: '자산코드', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'assetName', headerName: '자산명', flex: 1.2, headerClass: 'ag-header-center', cellClass: 'text-center' },
  {
    field: 'type', headerName: '구분', flex: 0.7, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => ACK_TYPE_LABEL[params.value] || params.value,
  },
  {
    field: 'memberId', headerName: '대상자', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => memberNameById?.[params.value] || params.value || '',
  },
  {
    field: 'status', headerName: '상태', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => ACK_STATUS_LABEL[params.value] || params.value,
  },
  {
    field: 'overdue', headerName: '기한', flex: 0.9, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => (params.value ? '기한초과' : formatDate({ value: params.data?.dueDate })),
    cellStyle: (params) => (params.value ? { color: '#d32f2f', fontWeight: 'bold' } : null),
  },
  { field: 'requestedAt', headerName: '요청일시', flex: 1.1, headerClass: 'ag-header-center', cellClass: 'text-center', valueFormatter: formatDateTime },
];

export const Grid = ({ rowData, isLoading, memberNameById, onRowSelect }) => {
  return (
    <WiniBox className="h-[560px]">
      <WiniAgGridReact
        rowData={rowData || []}
        columnDefs={buildColumnDefs(memberNameById)}
        getRowId={(params) => params.data.acknowledgementId}
        onRowClicked={(e) => onRowSelect?.(e?.data)}
        loading={isLoading}
      />
    </WiniBox>
  );
};
