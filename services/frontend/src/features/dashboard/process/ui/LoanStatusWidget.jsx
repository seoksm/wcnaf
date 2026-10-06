import HandshakeOutlinedIcon from '@mui/icons-material/HandshakeOutlined';
import { WiniBox } from '@/shared/ui/wini';
import { WidgetCard } from '../../common/ui/WidgetCard';
import { StatTile } from '../../common/ui/StatTile';

/** S-700 대여 현황 - 대여 프로세스 OFF면 부모(useDashboard)가 이 위젯 자체를 렌더링하지 않는다. */
export const LoanStatusWidget = ({ data }) => {
  if (!data) return null;

  return (
    <WidgetCard icon={HandshakeOutlinedIcon} title="대여 현황">
      <WiniBox className="flex gap-3">
        <StatTile label="대여중" value={data.activeCount ?? 0} />
        <StatTile label="연체" value={data.overdueCount ?? 0} color={data.overdueCount > 0 ? 'text-red-600' : 'text-text-main'} />
      </WiniBox>
    </WidgetCard>
  );
};
