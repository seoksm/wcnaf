import WarningAmberIcon from '@mui/icons-material/WarningAmberOutlined';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { formatDate } from '@/shared/lib/payment';
import { WidgetCard } from '../../common/ui/WidgetCard';
import { SEMANTIC_COLORS } from '../../common/model/chartColors';

/**
 * S-700 만료 예정 - 차트가 아니라 표(설계문서 §1: "이 위젯의 일은 무엇을 지금 처리해야
 * 하는가"). 라이선스는 만료일 개념이 없어(스키마 확인됨, 수량 기반) 무형자산만 대상이다.
 */
export const ExpiringSoonWidget = ({ data, onNavigate }) => {
  if (!data) return null;
  const items = data.items ?? [];

  return (
    <WidgetCard
      icon={WarningAmberIcon}
      title="만료 예정 (무형자산)"
      action={onNavigate && (
        <button type="button" onClick={onNavigate} className="text-xs text-primary-main hover:underline">
          목록 보기
        </button>
      )}
    >
      {/* div 사용 이유는 StatTile 주석과 동일 - WiniBox를 가로로 나열하면 첫 번째만
          auto-gap margin-top이 없어 나머지가 짧아 보이는 버그가 생긴다 */}
      <div className="flex gap-3">
        <div className="flex-1 rounded border border-solid border-gray-200 px-2 py-2 text-center">
          <WiniTypography variant="span" className="block text-xl font-bold" style={{ color: SEMANTIC_COLORS.critical }}>
            {data.within7Days ?? 0}
          </WiniTypography>
          <WiniTypography variant="span" className="block text-xs text-text-sub">7일 이내</WiniTypography>
        </div>
        <div className="flex-1 rounded border border-solid border-gray-200 px-2 py-2 text-center">
          <WiniTypography variant="span" className="block text-xl font-bold" style={{ color: SEMANTIC_COLORS.warning }}>
            {data.within30Days ?? 0}
          </WiniTypography>
          <WiniTypography variant="span" className="block text-xs text-text-sub">30일 이내</WiniTypography>
        </div>
        <div className="flex-1 rounded border border-solid border-gray-200 px-2 py-2 text-center">
          <WiniTypography variant="span" className="block text-xl font-bold text-text-main">
            {data.within90Days ?? 0}
          </WiniTypography>
          <WiniTypography variant="span" className="block text-xs text-text-sub">90일 이내</WiniTypography>
        </div>
      </div>

      {items.length > 0 && (
        <WiniBox className="mt-3 flex flex-col gap-1.5">
          {items.map((item) => (
            <WiniBox key={item.intangibleAssetId} className="flex items-center justify-between border-t border-solid border-gray-100 pt-1.5 text-xs">
              <WiniTypography variant="span" className="truncate text-text-main">{item.name}</WiniTypography>
              <WiniTypography
                variant="span"
                className="whitespace-nowrap font-medium"
                style={{ color: item.daysLeft < 0 ? SEMANTIC_COLORS.critical : item.daysLeft <= 30 ? SEMANTIC_COLORS.warning : undefined }}
              >
                {formatDate(item.expiryDate)} ({item.daysLeft < 0 ? `${-item.daysLeft}일 지남` : `D-${item.daysLeft}`})
              </WiniTypography>
            </WiniBox>
          ))}
        </WiniBox>
      )}
    </WidgetCard>
  );
};
