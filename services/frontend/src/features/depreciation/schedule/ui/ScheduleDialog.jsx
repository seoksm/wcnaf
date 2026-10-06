import {
  WiniBox,
  WiniButton,
  WiniChip,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniTypography,
} from '@/shared/ui/wini';
import { QUARTER_LABEL } from '@/entities/depreciation';

const formatNumber = (value) =>
  value == null ? '-' : Number(value).toLocaleString();

/**
 * 자산별 상각 스케줄 조회 다이얼로그 (S-231)
 */
export const ScheduleDialog = ({
  open,
  asset,
  schedule,
  isLoading,
  onClose,
}) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <WiniDialogTitle>
        자산별 상각 스케줄
        {asset ? ` - ${asset.assetName} (${asset.assetCode})` : ''}
      </WiniDialogTitle>

      <WiniDialogContent className="pt-4 pb-4">
        <WiniBox ui="noAutoGap" className="flex flex-col gap-2">
          {isLoading && (
            <WiniTypography
              variant="span"
              className="text-text-sub"
              aria-live="polite"
            >
              스케줄을 조회하고 있습니다.
            </WiniTypography>
          )}

          {!isLoading && (schedule || []).length === 0 && (
            <WiniTypography variant="span" className="text-text-sub">
              조회된 스케줄이 없습니다.
            </WiniTypography>
          )}

          {(schedule || []).map((row) => (
            <WiniBox
              key={row.quarter}
              ui="noAutoGap"
              className="flex items-center justify-between border rounded p-3 flex-wrap gap-2"
            >
              <WiniBox ui="noAutoGap" className="flex flex-col min-w-0 flex-1">
                <WiniTypography variant="span" className="font-semibold">
                  {row.fiscalYear}년 {QUARTER_LABEL[row.quarter] || row.quarter}
                </WiniTypography>
                <WiniTypography
                  variant="span"
                  className="text-text-sub text-sm"
                >
                  기초 {formatNumber(row.openingAccumulated)} / 당기{' '}
                  {formatNumber(row.periodDepreciation)} / 기말{' '}
                  {formatNumber(row.closingAccumulated)} / 장부가{' '}
                  {formatNumber(row.bookValue)}
                </WiniTypography>
              </WiniBox>

              <WiniBox
                ui="noAutoGap"
                className="flex items-center gap-1 shrink-0"
              >
                {row.excluded && (
                  <WiniChip label="제외" size="small" variant="outlined" />
                )}
                {row.confirmed ? (
                  <WiniChip label="확정" size="small" color="primary" />
                ) : (
                  <WiniChip label="미확정" size="small" variant="outlined" />
                )}
              </WiniBox>
            </WiniBox>
          ))}
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
