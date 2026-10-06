import * as React from 'react';
import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { LIFE_STATUS_LABEL, ASSIGN_TYPE_LABEL } from '@/entities/tangibleAsset';

const columnDefs = [
  {
    field: 'assetCode',
    headerName: '자산코드',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'assetName',
    headerName: '자산명',
    flex: 1.5,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'categoryName',
    headerName: '종류',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'locationName',
    headerName: '위치',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'lifeStatus',
    headerName: '생애상태',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => LIFE_STATUS_LABEL[params.value] || params.value,
  },
  {
    field: 'assignType',
    headerName: '배정형태',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => ASSIGN_TYPE_LABEL[params.value] || params.value,
  },
  {
    field: 'acquisitionAmount',
    headerName: '취득가액',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-right',
    valueFormatter: (params) =>
      params.value == null ? '' : Number(params.value).toLocaleString(),
  },
];

export const Grid = (props) => {
  return (
    <WiniBox className="h-[700px]">
      <WiniAgGridReact
        rowData={props.rowData || []}
        columnDefs={columnDefs}
        getRowId={(params) => params.data.tangibleAssetId}
        rowSelection={{
          mode: 'multiRow',
          checkboxes: true,
          headerCheckbox: true,
          enableClickSelection: false,
        }}
        onRowClicked={(e) => {
          if (!props.disabled) props.onRowSelect?.(e?.data);
        }}
        onSelectionChanged={(e) =>
          props.onCheckSelectionChange?.(e?.api?.getSelectedRows?.() || [])
        }
        isRowSelectable={() => !props.disabled}
        loading={props.isLoading}
      />
    </WiniBox>
  );
};
