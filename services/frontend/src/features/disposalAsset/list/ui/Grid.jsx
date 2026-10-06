import * as React from 'react';
import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { LIFE_STATUS_LABEL, DISPOSAL_REASON_CODE_LABEL } from '@/entities/tangibleAsset';

const formatAmount = (params) => (params.value == null ? '' : Number(params.value).toLocaleString());
const formatDateTime = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD HH:mm') : '');

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
    flex: 1.2,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'categoryName',
    headerName: '종류',
    flex: 0.8,
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
    field: 'bookValue',
    headerName: '장부가',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-right',
    valueFormatter: formatAmount,
  },
  {
    field: 'disposalReasonCode',
    headerName: '처분사유',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => (params.value ? DISPOSAL_REASON_CODE_LABEL[params.value] || params.value : ''),
  },
  {
    field: 'disposalAmount',
    headerName: '처분금액',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-right',
    valueFormatter: formatAmount,
  },
  {
    field: 'disposalGainLoss',
    headerName: '처분손익',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-right',
    valueFormatter: formatAmount,
  },
  {
    field: 'lifeStatusChangedAt',
    headerName: '상태전환일시',
    flex: 1.2,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: formatDateTime,
  },
];

export const Grid = (props) => {
  return (
    <WiniBox className="h-[700px]">
      <WiniAgGridReact
        rowData={props.rowData || []}
        columnDefs={columnDefs}
        getRowId={(params) => params.data.tangibleAssetId}
        onRowClicked={(e) => props.onRowSelect?.(e?.data)}
        loading={props.isLoading}
      />
    </WiniBox>
  );
};
