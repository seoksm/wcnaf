import * as React from 'react';
import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { EnumSelect } from '@/shared/ui/enum-select';
import {
  INVENTORY_RESULT_STATUS_LABEL,
  ANOMALY_TYPE_LABEL,
  CLOSURE_ACTION_LABEL,
} from '@/entities/inventory';

const formatAmount = (params) => (params.value == null ? '' : Number(params.value).toLocaleString());

const columnDefs = [
  { field: 'assetCode', headerName: '자산코드', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'assetName', headerName: '자산명', flex: 1.2, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'categoryName', headerName: '종류', flex: 0.8, headerClass: 'ag-header-center', cellClass: 'text-center' },
  {
    field: 'status',
    headerName: '상태',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => INVENTORY_RESULT_STATUS_LABEL[params.value] || params.value,
  },
  {
    field: 'anomalyType',
    headerName: '이상유형',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => (params.value ? ANOMALY_TYPE_LABEL[params.value] || params.value : ''),
  },
  {
    field: 'closureAction',
    headerName: '종결처리',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => (params.value ? CLOSURE_ACTION_LABEL[params.value] || params.value : ''),
  },
  { field: 'bookValue', headerName: '장부가', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-right', valueFormatter: formatAmount },
];

const STATUS_FILTER_LABEL = { ...INVENTORY_RESULT_STATUS_LABEL };

export const ResultsGrid = ({ statusFilter, onStatusFilterChange, rowData, isLoading, onRowSelect, onCheckSelectionChange }) => {
  return (
    <WiniBox>
      <WiniBox className="mb-2 flex justify-end">
        <EnumSelect
          ui="row"
          label="상태"
          name="statusFilter"
          value={statusFilter || ''}
          enums={STATUS_FILTER_LABEL}
          showAll
          allLabel="전체"
          className="w-40"
          onChange={onStatusFilterChange}
        />
      </WiniBox>
      <WiniBox className="h-[500px]">
        <WiniAgGridReact
          rowData={rowData || []}
          columnDefs={columnDefs}
          getRowId={(params) => params.data.inventoryTargetId}
          rowSelection={{ mode: 'multiRow', checkboxes: true, headerCheckbox: true, enableClickSelection: false }}
          onRowClicked={(e) => onRowSelect?.(e?.data)}
          onSelectionChanged={(e) => onCheckSelectionChange?.(e?.api?.getSelectedRows?.() || [])}
          loading={isLoading}
        />
      </WiniBox>
    </WiniBox>
  );
};
