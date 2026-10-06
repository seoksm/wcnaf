import { useCallback } from 'react';
import {
  WiniAgGridReact,
  WiniBox,
  WiniButton,
  WiniDateTimePicker,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniGridItem,
  WiniGridLayout,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';
import { EnumSelect } from '@/shared/ui/enum-select';
import { winiDate } from '@/shared/lib';
import { HISTORY_TYPE_LABEL } from '@/entities/tangibleAsset';

const formatDateTime = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD HH:mm') : '-');
const shortenId = (id) => (id ? `${id.slice(0, 8)}…` : '-');

const summarizeChanges = (changedFields) => {
  try {
    const parsed = JSON.parse(changedFields || '[]');
    if (!Array.isArray(parsed) || parsed.length === 0) return '-';
    return parsed.map((change) => `${change.field}: ${change.before ?? '(없음)'} → ${change.after ?? '(없음)'}`).join(', ');
  } catch {
    return '-';
  }
};

const columnDefs = [
  { field: 'createAt', headerName: '시각', minWidth: 145, valueFormatter: ({ value }) => formatDateTime(value) },
  { field: 'historyType', headerName: '유형', minWidth: 110, valueFormatter: ({ value }) => HISTORY_TYPE_LABEL[value] || value },
  { field: 'assetCode', headerName: '자산코드', minWidth: 130 },
  { field: 'assetName', headerName: '자산명', minWidth: 150 },
  { field: 'changedFields', headerName: '변경 내역', flex: 1, minWidth: 260, valueFormatter: ({ value }) => summarizeChanges(value) },
  { field: 'batchId', headerName: '배치', minWidth: 100, valueFormatter: ({ value }) => shortenId(value) },
];

export const ActivityLogDialog = ({
  open,
  filterDraft,
  onChangeFilter,
  onChangeDateFilter,
  onSearch,
  log,
  isLoading,
  hasMore,
  loadMore,
  onClose,
}) => {
  const rowCount = log?.length || 0;
  const handleScrollEnd = useCallback(
    (event) => {
      const lastVisibleRow = event.api.getLastDisplayedRowIndex?.() ?? -1;
      if (rowCount > 0 && lastVisibleRow >= rowCount - 2) loadMore();
    },
    [loadMore, rowCount],
  );

  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="lg">
      <WiniDialogTitle>전체 활동 로그</WiniDialogTitle>
      <WiniDialogContent className="pb-4 pt-4">
        <WiniBox ui="search">
          <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
            <WiniGridItem size={{ lg: 2, md: 4, xs: 12 }}>
              <EnumSelect label="이력 유형" name="historyType" value={filterDraft.historyType} enums={HISTORY_TYPE_LABEL} showAll allLabel="전체" onChange={onChangeFilter} />
            </WiniGridItem>
            <WiniGridItem size={{ lg: 2, md: 4, xs: 12 }}>
              <WiniText ui="row" label="자산코드" name="assetCode" value={filterDraft.assetCode || ''} onChange={onChangeFilter} />
            </WiniGridItem>
            <WiniGridItem size={{ lg: 2, md: 4, xs: 12 }}>
              <WiniText ui="row" label="자산명" name="assetName" value={filterDraft.assetName || ''} onChange={onChangeFilter} />
            </WiniGridItem>
            <WiniGridItem size={{ lg: 2, md: 4, xs: 12 }}>
              <WiniDateTimePicker label="발생시각 시작" format="YYYY-MM-DD HH:mm" value={filterDraft.fromDate} onChange={onChangeDateFilter('fromDate')} />
            </WiniGridItem>
            <WiniGridItem size={{ lg: 2, md: 4, xs: 12 }}>
              <WiniDateTimePicker label="종료" format="YYYY-MM-DD HH:mm" value={filterDraft.toDate} onChange={onChangeDateFilter('toDate')} />
            </WiniGridItem>
            <WiniGridItem size={{ lg: 2, md: 4, xs: 12 }} className="flex items-end justify-end">
              <WiniButton onClick={onSearch} loading={isLoading}>조회</WiniButton>
            </WiniGridItem>
          </WiniGridLayout>
        </WiniBox>

        <WiniTypography variant="span" className="my-2 block text-xs text-gray-400">
          최신 200건씩 조회하며, 목록 끝에 도달하면 다음 200건을 불러옵니다.
        </WiniTypography>
        <WiniBox className="h-[480px] min-h-[320px]">
          <WiniAgGridReact
            rowData={log || []}
            columnDefs={columnDefs}
            getRowId={({ data }) => data.assetHistoryId}
            loading={isLoading}
            onBodyScrollEnd={handleScrollEnd}
          />
        </WiniBox>
        {!isLoading && !hasMore && rowCount > 0 && (
          <WiniTypography variant="span" className="mt-2 block text-center text-xs text-gray-400">마지막 페이지입니다.</WiniTypography>
        )}
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="lineGray" onClick={onClose}>닫기</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
