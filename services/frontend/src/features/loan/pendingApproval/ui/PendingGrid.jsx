import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';

const formatDate = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD') : '');
const formatDateTime = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD HH:mm') : '');

const buildColumnDefs = (memberNameById) => [
  { field: 'assetCode', headerName: '자산코드', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'assetName', headerName: '자산명', flex: 1.2, headerClass: 'ag-header-center', cellClass: 'text-center' },
  {
    field: 'memberId', headerName: '신청자', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => memberNameById?.[params.value] || params.value || '',
  },
  { field: 'dueDate', headerName: '반납기한', flex: 0.9, headerClass: 'ag-header-center', cellClass: 'text-center', valueFormatter: formatDate },
  { field: 'borrowedAt', headerName: '신청일시', flex: 1.1, headerClass: 'ag-header-center', cellClass: 'text-center', valueFormatter: formatDateTime },
];

/** S-411 승인대기 - 행 클릭으로 선택하면 아래 액션 바(승인/반려)가 활성화된다 */
export const PendingGrid = ({ rowData, isLoading, memberNameById, onRowSelect }) => {
  return (
    <WiniBox className="h-[500px]">
      <WiniAgGridReact
        rowData={rowData || []}
        columnDefs={buildColumnDefs(memberNameById)}
        getRowId={(params) => params.data.loanId}
        onRowClicked={(e) => onRowSelect?.(e?.data)}
        loading={isLoading}
      />
    </WiniBox>
  );
};
