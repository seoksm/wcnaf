import ConfirmationNumberOutlinedIcon from '@mui/icons-material/ConfirmationNumberOutlined';
import { WiniBox } from '@/shared/ui/wini';
import { WidgetCard } from '../../common/ui/WidgetCard';
import { StatTile } from '../../common/ui/StatTile';

/**
 * S-700 티켓 현황 KPI 4칸 - Q-51 SLA 초과를 위험색으로 강조해 "조치 필요"를 즉시 드러낸다.
 */
export const TicketStatusWidget = ({ data, onNavigate }) => {
  if (!data) return null;

  return (
    <WidgetCard
      icon={ConfirmationNumberOutlinedIcon}
      title="티켓 현황"
      action={onNavigate && (
        <button type="button" onClick={onNavigate} className="text-xs text-primary-main hover:underline">
          칸반 보기
        </button>
      )}
    >
      {/* AssetSummaryWidget과 동일한 이유(및 62.5% 루트 폰트크기 함정 - 이름 있는 컨테이너
          크기 대신 픽셀 임의값을 쓴다)로 뷰포트 대신 컨테이너 쿼리 사용 */}
      <WiniBox className="grid grid-cols-2 gap-2 @min-[576px]:grid-cols-4">
        <StatTile label="접수대기" value={data.waitingCount ?? 0} size="text-xl" />
        <StatTile label="처리중" value={data.inProgressCount ?? 0} size="text-xl" />
        <StatTile label="SLA 초과" value={data.overdueCount ?? 0} size="text-xl" color="text-red-600" />
        <StatTile label="최근 7일 완료" value={data.doneThisWeekCount ?? 0} size="text-xl" color="text-green-600" />
      </WiniBox>
    </WidgetCard>
  );
};
