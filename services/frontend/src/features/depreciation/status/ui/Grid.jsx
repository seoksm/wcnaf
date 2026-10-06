import * as React from 'react';
import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { EXCLUDED_REASON_LABEL } from '@/entities/depreciation';

const numberFormatter = (params) =>
  params.value == null ? '' : Number(params.value).toLocaleString();
const dateFormatter = (params) =>
  params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD') : '';

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
    flex: 1.3,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'categoryName',
    headerName: '종류',
    flex: 0.9,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
  {
    field: 'acquisitionDate',
    headerName: '취득일',
    flex: 0.9,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: dateFormatter,
  },
  {
    field: 'acquisitionAmount',
    headerName: '취득가액',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-right',
    valueFormatter: numberFormatter,
  },
  {
    field: 'openingAccumulated',
    headerName: '기초 누계액',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-right',
    valueFormatter: numberFormatter,
  },
  {
    field: 'periodDepreciation',
    headerName: '당기 상각액',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-right',
    valueFormatter: numberFormatter,
  },
  {
    field: 'closingAccumulated',
    headerName: '기말 누계액',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-right',
    valueFormatter: numberFormatter,
  },
  {
    field: 'bookValue',
    headerName: '장부가',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-right',
    valueFormatter: numberFormatter,
  },
  {
    field: 'elapsedMonths',
    headerName: '경과/내용연수(개월)',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) =>
      params.value == null
        ? '-'
        : `${params.value}/${params.data?.usefulLifeMonths ?? '-'}`,
  },
  {
    field: 'excludedReason',
    headerName: '상각 제외 사유',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) =>
      params.value ? EXCLUDED_REASON_LABEL[params.value] || params.value : '',
  },
];

/**
 * 감가상각 현황 목록 (S-230) - 행 클릭 시 자산별 스케줄 조회(S-231)로 연결
 */
export const Grid = (props) => {
  return (
    <WiniBox className="h-[600px]">
      <WiniAgGridReact
        rowData={props.rowData || []}
        columnDefs={columnDefs}
        loading={props.isLoading}
        getRowId={(params) => params.data.tangibleAssetId}
        onRowClicked={(e) => {
          if (!props.isLoading) props.onRowSelect?.(e?.data);
        }}
      />
    </WiniBox>
  );
};
