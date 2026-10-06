import * as React from 'react';
import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { INVENTORY_TYPE_LABEL, INVENTORY_STATUS_LABEL } from '@/entities/inventory';

const formatDateTime = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD HH:mm') : '');

const columnDefs = [
  {
    field: 'title',
    headerName: '조사명',
    flex: 1.5,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'inventoryType',
    headerName: '유형',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => INVENTORY_TYPE_LABEL[params.value] || params.value,
  },
  {
    field: 'status',
    headerName: '상태',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => INVENTORY_STATUS_LABEL[params.value] || params.value,
  },
  {
    field: 'targetCount',
    headerName: '대상 자산 수',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-right',
    valueFormatter: (params) => (params.value == null ? '' : Number(params.value).toLocaleString()),
  },
  {
    field: 'createAt',
    headerName: '생성일시',
    flex: 1.2,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: formatDateTime,
  },
  {
    field: 'closedAt',
    headerName: '종료일시',
    flex: 1.2,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: formatDateTime,
  },
];

export const Grid = (props) => {
  return (
    <WiniBox className="h-[600px]">
      <WiniAgGridReact
        rowData={props.rowData || []}
        columnDefs={columnDefs}
        getRowId={(params) => params.data.inventoryId}
        onRowClicked={(e) => props.onRowSelect?.(e?.data)}
        loading={props.isLoading}
      />
    </WiniBox>
  );
};
