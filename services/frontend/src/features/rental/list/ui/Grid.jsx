import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { BILLING_MODE_LABEL, RENTAL_STATUS_LABEL } from '@/entities/rental';

const formatDate = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD') : '');

const buildColumnDefs = () => [
  { field: 'name', headerName: '렌탈·구독명', flex: 1.4, headerClass: 'ag-header-center', cellClass: 'text-center' },
  {
    field: 'billingMode', headerName: '결제방식', flex: 0.9, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => BILLING_MODE_LABEL[params.value] || params.value,
  },
  { field: 'startDate', headerName: '시작일', flex: 0.9, headerClass: 'ag-header-center', cellClass: 'text-center', valueFormatter: formatDate },
  { field: 'endDate', headerName: '종료일', flex: 0.9, headerClass: 'ag-header-center', cellClass: 'text-center', valueFormatter: formatDate },
  {
    field: 'status', headerName: '상태', flex: 0.8, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => RENTAL_STATUS_LABEL[params.value] || params.value,
  },
];

export const Grid = ({ rowData, isLoading, onRowSelect }) => {
  return (
    <WiniBox className="h-[560px]">
      <WiniAgGridReact
        rowData={rowData || []}
        columnDefs={buildColumnDefs()}
        getRowId={(params) => params.data.rentalAssetId}
        onRowClicked={(e) => onRowSelect?.(e?.data)}
        loading={isLoading}
      />
    </WiniBox>
  );
};
