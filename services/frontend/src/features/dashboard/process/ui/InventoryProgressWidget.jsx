import FactCheckOutlinedIcon from '@mui/icons-material/FactCheckOutlined';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { WidgetCard } from '../../common/ui/WidgetCard';
import { SEMANTIC_COLORS } from '../../common/model/chartColors';

/** S-700 전수조사 진행률 - 미터 + 참여자 수. 진행 중인 조사가 여러 건이면(유형별 동시 진행) 각각 표시한다. */
export const InventoryProgressWidget = ({ items }) => {
  const rows = items ?? [];

  return (
    <WidgetCard icon={FactCheckOutlinedIcon} title="전수조사 진행률">
      {rows.length === 0 ? (
        <WiniTypography variant="span" className="text-xs text-text-sub">진행 중인 전수조사가 없습니다.</WiniTypography>
      ) : (
        <WiniBox className="flex flex-col gap-3">
          {rows.map((row) => {
            const percent = row.totalCount > 0 ? Math.round((row.confirmedCount / row.totalCount) * 100) : 0;
            return (
              <WiniBox key={row.inventoryId}>
                <WiniBox className="mb-1 flex items-center justify-between">
                  <WiniTypography variant="span" className="text-xs font-medium text-text-main">{row.title}</WiniTypography>
                  <WiniTypography variant="span" className="text-xs text-text-sub">
                    {row.confirmedCount}/{row.totalCount}건 · 참여 {row.participantCount}명
                  </WiniTypography>
                </WiniBox>
                <WiniBox className="h-3 w-full overflow-hidden rounded-full bg-gray-eee">
                  <div style={{ width: `${percent}%`, backgroundColor: SEMANTIC_COLORS.normal, height: '100%' }} />
                </WiniBox>
                <WiniTypography variant="span" className="mt-0.5 block text-right text-xs text-text-sub">{percent}%</WiniTypography>
              </WiniBox>
            );
          })}
        </WiniBox>
      )}
    </WidgetCard>
  );
};
