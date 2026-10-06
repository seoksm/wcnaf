import DonutSmallOutlinedIcon from '@mui/icons-material/DonutSmallOutlined';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { WidgetCard } from '../../common/ui/WidgetCard';
import { LIFE_STATUS_COLORS } from '../../common/model/chartColors';

const colorOf = (name) => LIFE_STATUS_COLORS[name] ?? '#9ca3af';

/**
 * S-700 상태별 현황 - 원형차트 대신 단일 누적 막대 1줄 + 범례(설계문서 §1: "부분-전체+상태"는
 * 파이가 아니라 누적막대). 세그먼트가 너무 얇아 안 보이는 상태도 범례에서는 항상 보이게 한다.
 */
export const StatusDistributionWidget = ({ items }) => {
  const rows = items ?? [];
  const total = rows.reduce((sum, row) => sum + row.count, 0);

  return (
    <WidgetCard icon={DonutSmallOutlinedIcon} title="상태별 현황">
      {total === 0 ? (
        <WiniTypography variant="span" className="text-xs text-text-sub">데이터가 없습니다.</WiniTypography>
      ) : (
        <>
          <WiniBox className="flex h-6 w-full overflow-hidden rounded bg-gray-eee">
            {rows.filter((row) => row.count > 0).map((row) => (
              <div
                key={row.name}
                title={`${row.name} ${row.count}건 (${Math.round((row.count / total) * 100)}%)`}
                style={{ width: `${(row.count / total) * 100}%`, backgroundColor: colorOf(row.name) }}
              />
            ))}
          </WiniBox>
          {/* div 사용 이유는 StatTile 주석과 동일 - WiniBox를 나열하면 첫 항목만 auto-gap이
              없어 나머지가 밀려 보이는 버그가 생긴다 */}
          <div className="mt-3 flex flex-wrap gap-x-4 gap-y-1.5">
            {rows.map((row) => (
              <div key={row.name} className="flex items-center gap-1.5">
                <span
                  className="inline-block h-2.5 w-2.5 rounded-full"
                  style={{ backgroundColor: colorOf(row.name) }}
                />
                <WiniTypography variant="span" className="text-xs text-text-main">
                  {row.name} {row.count}건
                </WiniTypography>
              </div>
            ))}
          </div>
        </>
      )}
    </WidgetCard>
  );
};
