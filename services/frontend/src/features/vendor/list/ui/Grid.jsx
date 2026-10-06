import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';

const columnDefs = [
  { field: 'name', headerName: '공급사명', flex: 1.4, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'contactName', headerName: '담당자', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'contactPhone', headerName: '연락처', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'contactEmail', headerName: '이메일', flex: 1.2, headerClass: 'ag-header-center', cellClass: 'text-center' },
];

export const Grid = ({ rowData, isLoading, disabled, onRowSelect }) => {
  return (
    <WiniBox className="h-[560px]">
      <WiniAgGridReact
        rowData={rowData || []}
        columnDefs={columnDefs}
        getRowId={(params) => params.data.vendorId}
        onRowClicked={(e) => {
          if (!disabled) onRowSelect?.(e?.data);
        }}
        loading={isLoading}
      />
    </WiniBox>
  );
};
