import VerifiedOutlinedIcon from '@mui/icons-material/VerifiedOutlined';
import { WiniBox } from '@/shared/ui/wini';
import { WidgetCard } from '../../common/ui/WidgetCard';
import { StatTile } from '../../common/ui/StatTile';

/** S-700 라이선스 정합성 - Q-47: 초과는 차단이 아니라 경고. 0건이면 녹색으로 "문제 없음"을 보여준다. */
export const LicenseConsistencyWidget = ({ data }) => {
  if (!data) return null;
  const overColor = data.overCapacityLicenseCount > 0 ? 'text-red-600' : 'text-green-600';
  const unlinkedColor = data.unlinkedAssignmentCount > 0 ? 'text-orange-600' : 'text-green-600';

  return (
    <WidgetCard icon={VerifiedOutlinedIcon} title="라이선스 정합성">
      <WiniBox className="flex gap-3">
        <StatTile label="초과 배정 종수" value={data.overCapacityLicenseCount ?? 0} color={overColor} />
        <StatTile label="미연결 배정" value={data.unlinkedAssignmentCount ?? 0} color={unlinkedColor} />
      </WiniBox>
    </WidgetCard>
  );
};
