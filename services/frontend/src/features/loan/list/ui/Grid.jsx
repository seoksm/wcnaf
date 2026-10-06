import { WiniAgGridReact, WiniBox } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { LOAN_STATUS_LABEL } from '@/entities/loan';

const formatDate = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD') : '');
const formatDateTime = (params) => (params.value ? winiDate.dateFormat(winiDate(params.value), 'YYYY-MM-DD HH:mm') : '');

/** 오늘과 반납기한을 같은 tz 파이프라인(dateParseStartOf)으로 하루 단위 절삭해 비교한다 -
 * new Date(dueDate) 문자열 파싱은 UTC 자정으로, new Date()(현재)는 로컬 시각으로 해석돼
 * 그 둘을 그대로 빼면 시간대 경계에서 연체일이 하루 어긋날 수 있다. */
const overdueDays = (dueDate) => {
  if (!dueDate) return 0;
  const today = winiDate.dateParseStartOf(winiDate.now());
  const due = winiDate.dateParseStartOf(dueDate);
  return Math.max(0, today.diff(due, 'day'));
};

const buildColumnDefs = (memberNameById) => [
  { field: 'assetCode', headerName: '자산코드', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'assetName', headerName: '자산명', flex: 1.2, headerClass: 'ag-header-center', cellClass: 'text-center' },
  { field: 'categoryName', headerName: '종류', flex: 0.8, headerClass: 'ag-header-center', cellClass: 'text-center' },
  {
    field: 'memberId', headerName: '대여자', flex: 1, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => memberNameById?.[params.value] || params.value || '',
  },
  {
    field: 'status', headerName: '상태', flex: 0.8, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => LOAN_STATUS_LABEL[params.value] || params.value,
  },
  { field: 'dueDate', headerName: '반납기한', flex: 0.9, headerClass: 'ag-header-center', cellClass: 'text-center', valueFormatter: formatDate },
  {
    // cellDataType: false - ag-Grid 32의 컬럼 타입 자동추론이 boolean 값을 보고 체크박스
    // 렌더러로 바꿔치기해 valueFormatter("연체 N일")가 화면에 전혀 안 보이던 문제를 막는다
    // (발견: 이 probe 검증 중 실제로 체크박스만 뜨고 텍스트가 없는 것을 확인함).
    field: 'overdue', headerName: '연체', flex: 0.8, headerClass: 'ag-header-center', cellClass: 'text-center', cellDataType: false,
    valueFormatter: (params) => (params.value ? `연체 ${overdueDays(params.data?.dueDate)}일` : ''),
    cellClassRules: {
      'text-state-error': (params) => Boolean(params.value),
      'font-bold': (params) => Boolean(params.value),
    },
  },
  {
    field: 'extendCount', headerName: '연장', flex: 0.6, headerClass: 'ag-header-center', cellClass: 'text-center',
    valueFormatter: (params) => `${params.value ?? 0}회`,
  },
  { field: 'borrowedAt', headerName: '대여일시', flex: 1.1, headerClass: 'ag-header-center', cellClass: 'text-center', valueFormatter: formatDateTime },
];

export const Grid = ({ rowData, isLoading, memberNameById }) => {
  return (
    <WiniBox className="h-[600px]">
      <WiniAgGridReact
        rowData={rowData || []}
        columnDefs={buildColumnDefs(memberNameById)}
        getRowId={(params) => params.data.loanId}
        loading={isLoading}
      />
    </WiniBox>
  );
};
