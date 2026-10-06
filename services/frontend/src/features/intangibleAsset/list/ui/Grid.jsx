import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { INTANGIBLE_TYPE_LABEL } from '@/entities/intangibleAsset';

const formatDate = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD') : '');

const buildColumnDefs = () => [
  {
    field: 'intangibleType', headerName: '구분', flex: 0.7, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => INTANGIBLE_TYPE_LABEL[params.value] || params.value,
  },
  { field: 'name', headerName: '자산명', flex: 1.3, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'issuer', headerName: '발급기관', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'expiryDate', headerName: '만료일', flex: 0.9, headerClass: 'ag-header-center', cellClass: 'text-center', valueFormatter: formatDate },
  {
    field: 'daysUntilExpiry', headerName: 'D-day', flex: 0.7, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => (params.value >= 0 ? `D-${params.value}` : `D+${Math.abs(params.value)}`),
    cellClassRules: {
      'text-state-error': (params) => Boolean(params.data?.expired || params.data?.nearExpiry),
      'font-bold': (params) => Boolean(params.data?.expired || params.data?.nearExpiry),
    },
  },
  {
    field: 'alertDays', headerName: '알림설정', flex: 0.8, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => (params.value ? params.value.split(',').join('·') : '-'),
  },
];

export const Grid = ({ rowData, isLoading, onRowSelect }) => {
  return (
    <WiniBox className="h-[560px]">
      <WiniAgGridReact
        rowData={rowData || []}
        columnDefs={buildColumnDefs()}
        getRowId={(params) => params.data.intangibleAssetId}
        onRowClicked={(e) => onRowSelect?.(e?.data)}
        loading={isLoading}
      />
    </WiniBox>
  );
};
