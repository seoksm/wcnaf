import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';

const columnDefs = [
  { field: 'name', headerName: '소프트웨어명', flex: 1.4, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'publisher', headerName: '제조사', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'category', headerName: '분류', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center' },
];

export const Grid = ({ rowData, isLoading, disabled, onRowSelect }) => {
  return (
    <WiniBox className="h-[560px]">
      <WiniAgGridReact
        rowData={rowData || []}
        columnDefs={columnDefs}
        getRowId={(params) => params.data.softwareId}
        onRowClicked={(e) => {
          if (!disabled) onRowSelect?.(e?.data);
        }}
        loading={isLoading}
      />
    </WiniBox>
  );
};
