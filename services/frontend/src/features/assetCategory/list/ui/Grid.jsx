import * as React from 'react';
import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';

const columnDefs = [
  {
    field: 'categoryCode',
    headerName: '종류 코드',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'categoryName',
    headerName: '종류명',
    flex: 1.5,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'sortSeq',
    headerName: '정렬순서',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'usefulLifeMonths',
    headerName: '내용연수(개월)',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'residualRate',
    headerName: '잔존가치율(%)',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'editable',
    headerName: '기본제공',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => (params.value === false ? '기본' : '사용자'),
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
