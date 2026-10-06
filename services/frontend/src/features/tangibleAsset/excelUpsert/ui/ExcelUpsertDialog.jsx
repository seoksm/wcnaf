import {
  WiniAgGridReact,
  WiniBox,
  WiniButton,
  WiniChip,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniTypography,
} from '@/shared/ui/wini';

const ACTION_LABEL = { CREATE: '신규', UPDATE: '수정', ERROR: '오류' };
const ACTION_COLOR = { CREATE: 'success', UPDATE: 'primary', ERROR: 'error' };

const columnDefs = [
  { field: 'rowNum', headerName: '행', width: 80 },
  {
    field: 'action',
    headerName: '구분',
    width: 100,
    cellRenderer: ({ value }) => (
      <WiniChip size="small" color={ACTION_COLOR[value] || 'default'} label={ACTION_LABEL[value] || value} />
    ),
  },
  { field: 'assetCode', headerName: '자산코드', minWidth: 140, valueFormatter: ({ value }) => value || '-' },
  {
    headerName: '자산명 / 오류사유',
    flex: 1,
    minWidth: 260,
    valueGetter: ({ data }) => (data.action === 'ERROR' ? data.errorMessage : data.assetName),
    cellClass: ({ data }) => (data.action === 'ERROR' ? 'text-red-600' : ''),
  },
];

export const ExcelUpsertDialog = ({
  open,
  file,
  rows,
  isPreviewing,
  isCommitting,
  summary,
  onFileChange,
  onClose,
  onDownloadTemplate,
  onPreview,
  onCommit,
}) => {
  const errorCount = rows.filter((row) => row.action === 'ERROR').length;
  const applicableCount = rows.length - errorCount;
  const isBusy = isPreviewing || isCommitting;

  return (
    <WiniDialog open={open} onClose={isBusy ? undefined : onClose} fullWidth maxWidth="md">
      <WiniDialogTitle>유형자산 엑셀 업서트</WiniDialogTitle>
      <WiniDialogContent className="pb-4 pt-4">
        <WiniBox className="flex flex-col gap-3">
          <WiniBox ui="btnbox">
            <WiniBox ui="btnitem">
              <WiniButton ui="lineGray" onClick={onDownloadTemplate} disabled={isBusy}>양식 다운로드</WiniButton>
              <WiniButton ui="lineGray" component="label" disabled={isBusy}>
                파일 선택
                <input className="sr-only" type="file" accept=".xlsx" onChange={onFileChange} />
              </WiniButton>
              <WiniTypography variant="span" className="max-w-72 truncate text-sm text-text-sub">
                {file?.name || '선택된 파일 없음'}
              </WiniTypography>
            </WiniBox>
            <WiniBox ui="btnitem">
              <WiniButton onClick={onPreview} loading={isPreviewing} disabled={!file || isBusy}>미리보기</WiniButton>
            </WiniBox>
          </WiniBox>

          <WiniBox ui="info" className="p-3">
            <WiniTypography variant="span" className="text-xs text-gray-500">
              자산코드를 비워두면 신규 등록, 자산코드가 있으면 해당 자산을 수정합니다. 빈 칸은 기존 값을 유지합니다(신규 등록 시 필수 항목은 예외).
            </WiniTypography>
          </WiniBox>

          {rows.length > 0 && (
            <>
              <WiniTypography variant="span" className="text-sm">
                총 {rows.length}건 · 반영 가능 {applicableCount}건 · 오류 {errorCount}건
              </WiniTypography>
              <WiniBox className="h-[320px] min-h-[240px]">
                <WiniAgGridReact rowData={rows} columnDefs={columnDefs} getRowId={({ data }) => String(data.rowNum)} />
              </WiniBox>
              <WiniBox ui="btnbox">
                <WiniBox ui="btnitem" />
                <WiniBox ui="btnitem">
                  <WiniButton onClick={onCommit} loading={isCommitting} disabled={applicableCount === 0 || isBusy}>확정</WiniButton>
                </WiniBox>
              </WiniBox>
            </>
          )}

          {summary && (
            <WiniBox ui="info" className="p-3">
              <WiniTypography variant="span" className="text-sm">
                신규 {summary.createdCount}건, 수정 {summary.updatedCount}건 반영 완료
                {summary.skippedCount > 0 && ` (오류로 제외된 ${summary.skippedCount}건)`}
              </WiniTypography>
              {summary.failedRows?.length > 0 && (
                <WiniBox className="mt-2 text-red-600">
                  <WiniTypography variant="span" className="block text-sm">반영 실패 {summary.failedRows.length}건 - 행별 사유를 확인하고 다시 미리보기해주세요.</WiniTypography>
                  <ul className="mt-1 list-disc pl-4 text-sm">
                    {summary.failedRows.map((row) => <li key={row.rowNum}>{row.rowNum}행: {row.errorMessage}</li>)}
                  </ul>
                </WiniBox>
              )}
            </WiniBox>
          )}
        </WiniBox>
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="lineGray" onClick={onClose} disabled={isBusy}>닫기</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
