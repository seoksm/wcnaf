import { WiniBox, WiniChip, WiniTypography } from '@/shared/ui/wini';

const formatNumber = (value) =>
  value == null ? '-' : Number(value).toLocaleString();

const SummaryItem = ({ label, value }) => (
  <WiniBox ui="noAutoGap" className="flex flex-col items-center px-4">
    <WiniTypography variant="span" className="text-text-sub text-sm">
      {label}
    </WiniTypography>
    <WiniTypography variant="span" className="text-text-default font-semibold">
      {formatNumber(value)}
    </WiniTypography>
  </WiniBox>
);

/**
 * 감가상각 현황 요약(총액법) 바 - S-230
 */
export const Summary = ({ summary }) => {
  if (!summary) return null;

  return (
    <WiniBox className="flex items-center justify-between border rounded p-3 mb-2 flex-wrap gap-2">
      <WiniBox ui="noAutoGap" className="flex items-center flex-wrap">
        <SummaryItem
          label="취득가액 합계"
          value={summary.totalAcquisitionAmount}
        />
        <SummaryItem
          label="기초 누계액"
          value={summary.totalOpeningAccumulated}
        />
        <SummaryItem
          label="당기 상각액"
          value={summary.totalPeriodDepreciation}
        />
        <SummaryItem
          label="기말 누계액"
          value={summary.totalClosingAccumulated}
        />
        <SummaryItem label="장부가 합계" value={summary.totalBookValue} />
        <SummaryItem label="상각 제외 건수" value={summary.excludedCount} />
      </WiniBox>

      <WiniBox ui="noAutoGap" className="flex items-center gap-2">
        {summary.confirmed ? (
          <WiniChip label="확정됨" color="primary" />
        ) : (
          <WiniChip label="미확정(실시간 산출)" variant="outlined" />
        )}
      </WiniBox>
    </WiniBox>
  );
};
