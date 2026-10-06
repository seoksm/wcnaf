import InventoryIcon from '@mui/icons-material/Inventory2Outlined';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { formatAmount, formatDateTime } from '@/shared/lib/payment';
import { WidgetCard } from '../../common/ui/WidgetCard';
import { StatTile } from '../../common/ui/StatTile';

/**
 * S-700 자산 총계 (요약) - DashboardServiceImpl이 S-230(감가상각 현황)과 동일한 계산을
 * 재사용하므로, 이 위젯의 숫자는 항상 S-230과 일치한다(D7: 확정 기간은 스냅샷, 미확정은 실시간).
 */
export const AssetSummaryWidget = ({ data }) => {
  if (!data) return null;

  return (
    <WidgetCard icon={InventoryIcon} title="자산 총계">
      {/* 설계문서 §2 "<768은 2×2" 기준 - 뷰포트가 아니라 실제 콘텐츠 폭(사이드바를 뺀 나머지)을
          기준으로 판단해야 해서 뷰포트 브레이크포인트(md:) 대신 컨테이너 쿼리를 쓴다
          (WiniFormNormal이 @container를 열어둔다). 이름 있는 컨테이너 크기(@xl 등) 대신
          꼭 픽셀 임의값(@min-[]:)을 쓴다 - @container의 rem은 @media와 달리 브라우저
          기본 루트 폰트크기가 아니라 "지금 실제로 적용된" 루트 폰트크기를 기준으로 계산되는데,
          이 앱은 main.css에서 html { font-size: 62.5% }를 전역으로 깔아둬서 이름 있는
          rem 기준 컨테이너 브레이크포인트가 전부 62.5%로 쪼그라든다(예: @xl=36rem이
          576px가 아니라 360px에서 풀림) - 실제로 Playwright로 재현·확인했다. */}
      <WiniBox className="grid grid-cols-2 gap-3 @min-[576px]:grid-cols-4">
        <StatTile label="자산 수" value={`${data.assetCount ?? 0}건`} size="text-3xl" />
        <StatTile label="취득가액" value={formatAmount(data.totalAcquisitionAmount)} size="text-3xl" />
        <StatTile label="감가상각누계" value={formatAmount(data.totalAccumulatedDepreciation)} size="text-3xl" />
        <StatTile label="장부가" value={formatAmount(data.totalBookValue)} size="text-3xl" color="text-primary-main" />
      </WiniBox>
      <WiniTypography variant="span" className="mt-2 block text-right text-xs text-text-sub">
        {data.confirmed ? '결산 확정 기준' : '실시간 산출 기준'} · {formatDateTime(data.basedOn)}
      </WiniTypography>
    </WidgetCard>
  );
};
