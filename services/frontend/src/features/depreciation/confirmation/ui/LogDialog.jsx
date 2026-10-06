import {
  WiniBox,
  WiniButton,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniAgGridReact,
} from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { QUARTER_LABEL } from '@/entities/depreciation';

const columnDefs = [
  {
    field: 'fiscalYear',
    headerName: '회계연도',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => `${params.value}년`,
  },
  {
    field: 'quarter',
    headerName: '분기',
    flex: 0.8,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) => QUARTER_LABEL[params.value] || params.value,
  },
  {
    field: 'confirmedAt',
    headerName: '확정 일시',
    flex: 1.2,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
    valueFormatter: (params) =>
      params.value
        ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD HH:mm:ss')
        : '',
  },
  {
    field: 'confirmedBy',
    headerName: '확정자',
    flex: 1,
    headerClass: 'ag-header-center',
    cellClass: 'text-center',
  },
];

/**
 * 결산 확정·해제 이력 다이얼로그 (S-232)
 */
export const LogDialog = ({ open, log, isLoading, onClose }) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <WiniDialogTitle>확정·해제 이력</WiniDialogTitle>

      <WiniDialogContent className="pt-4 pb-4">
        <WiniBox className="h-[400px]">
          <WiniAgGridReact
            rowData={log || []}
            columnDefs={columnDefs}
            loading={isLoading}
            getRowId={(params) => params.data.id}
          />
        </WiniBox>
      </WiniDialogContent>

      <WiniDialogActions>
        <WiniButton ui="lineGray" onClick={onClose}>
          닫기
        </WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
