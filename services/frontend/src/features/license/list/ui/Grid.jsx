import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';

/** Q-47: 잔여가 음수면 초과배정 - 빨간색으로 경고만 하고 막지는 않는다 */
const remainingCellClass = (params) => (params.value < 0 ? 'text-center font-semibold text-red-600' : 'text-center');

const columnDefs = [
  { field: 'name', headerName: '라이선스명', flex: 1.4, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'purchasedQuantity', headerName: '보유', flex: 0.7, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'assignedQuantity', headerName: '배정', flex: 0.7, headerClass: 'ag-header-center', cellClass: 'text-center' },
  {
    field: 'remainingQuantity', headerName: '잔여', flex: 0.7, headerClass: 'ag-header-center', cellClass: remainingCellClass,
    valueFormatter: (params) => (params.data?.overAssigned ? `${params.value} (초과)` : params.value),
  },
  { field: 'memo', headerName: '메모', flex: 1.4, headerClass: 'ag-header-center', cellClass: 'text-center' },
];

export const Grid = ({ rowData, isLoading, onRowSelect }) => {
  return (
    <WiniBox className="h-[560px]">
      <WiniAgGridReact
        rowData={rowData || []}
        columnDefs={columnDefs}
        getRowId={(params) => params.data.licenseId}
        onRowClicked={(e) => onRowSelect?.(e?.data)}
        loading={isLoading}
      />
    </WiniBox>
  );
};
