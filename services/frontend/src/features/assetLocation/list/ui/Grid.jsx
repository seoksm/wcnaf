import * as React from 'react';
import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';

const columnDefs = [
  {
    field: 'locationName',
    headerName: '위치명',
    flex: 2,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'sortSeq',
    headerName: '정렬순서',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'status',
    headerName: '상태',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
];

export const Grid = (props) => {
  return (
    <WiniBox className="h-[700px]">
      <WiniAgGridReact
        rowData={props.rowData || []}
        columnDefs={columnDefs}
        loading={props.isLoading}
        onRowClicked={(e) => {
          if (!props.disabled) props.onRowSelect?.(e?.data);
        }}
      />
    </WiniBox>
  );
};
