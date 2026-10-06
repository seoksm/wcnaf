import { winiDate } from '@/shared/lib';
import { HISTORY_TYPE_LABEL } from '@/entities/tangibleAsset';
import { WiniBox, WiniChip, WiniTypography } from '@/shared/ui/wini';

const HISTORY_TYPE_COLOR = {
  REGISTER: 'success',
  MODIFY: 'primary',
  STATUS_CHANGE: 'warning',
  ASSIGNMENT: 'secondary',
  DISUSE: 'error',
};

const formatDateTime = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD HH:mm') : '-');

const parseChanges = (changedFields) => {
  try {
    const parsed = JSON.parse(changedFields || '[]');
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
};

/**
 * 유형자산 1건의 변경 이력 (S-220) - 항목/전/후 diff 카드 목록, 최신순
 */
export const AssetHistoryPanel = ({ history, isLoading }) => {
  return (
    <WiniBox ui="info" className="p-4">
      <WiniBox className="mb-3 flex items-center justify-between">
        <WiniTypography variant="span" className="text-sm font-bold text-text-main">변경 이력</WiniTypography>
      </WiniBox>

      {isLoading && <WiniTypography variant="span" className="block py-2 text-sm text-gray-400">불러오는 중...</WiniTypography>}
      {!isLoading && (!history || history.length === 0) && (
        <WiniTypography variant="span" className="block py-2 text-sm text-gray-400">변경 이력이 없습니다.</WiniTypography>
      )}

      <WiniBox className="flex flex-col gap-2">
        {(history || []).map((item) => {
          const changes = parseChanges(item.changedFields);
          return (
            <WiniBox
              key={item.assetHistoryId}
              className="rounded border border-solid border-gray-200 bg-white px-3 py-2"
            >
              <WiniBox className="flex items-center justify-between gap-2">
                <WiniChip size="small" color={HISTORY_TYPE_COLOR[item.historyType] || 'default'} label={HISTORY_TYPE_LABEL[item.historyType] || item.historyType} />
                <WiniTypography variant="span" className="text-xs text-gray-400">{formatDateTime(item.createAt)}</WiniTypography>
              </WiniBox>

              <WiniBox className="mt-1 flex flex-col gap-1">
                {changes.map((c, idx) => (
                  <WiniTypography variant="span" key={`${item.assetHistoryId}_${idx}`} className="text-xs text-text-sub">
                    <span className="font-medium">{c.field}</span>
                    {': '}
                    <span className="text-gray-400">{c.before ?? '(없음)'}</span>
                    {' → '}
                    <span>{c.after ?? '(없음)'}</span>
                  </WiniTypography>
                ))}
              </WiniBox>
            </WiniBox>
          );
        })}
      </WiniBox>
    </WiniBox>
  );
};
