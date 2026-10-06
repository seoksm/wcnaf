import { WiniBox, WiniTypography } from '@/shared/ui/wini';

const KPI_ITEMS = [
  { key: 'confirmedCount', label: '확인완료', color: 'text-green-600' },
  { key: 'pendingApprovalCount', label: '승인대기', color: 'text-blue-600' },
  { key: 'anomalyCount', label: '이상', color: 'text-red-600' },
  { key: 'unconfirmedCount', label: '미확인', color: 'text-gray-600' },
];

/**
 * S-303 진행 현황 4개 지표 + 참여자별 분해 - "미확인 N건 = 미참여 M명 + 부분완료 K명"처럼
 * 사람 단위로도 보여준다(자산 개수보다 "누구에게 연락해야 하는가"가 실제 행동 단위이기 때문).
 */
export const ProgressSummary = ({ progress }) => {
  if (!progress) return null;

  const participants = progress.participants || [];
  const notParticipated = participants.filter((p) => !p.participated).length;
  const partial = participants.filter((p) => p.participated && p.unconfirmedCount > 0).length;

  return (
    <WiniBox className="mb-3">
      <WiniBox className="flex gap-4">
        {KPI_ITEMS.map((item) => (
          <WiniBox ui="noAutoGap" key={item.key} className="flex-1 rounded border border-solid border-gray-200 bg-white px-3 py-2 text-center">
            <WiniTypography variant="span" className={`block text-2xl font-bold ${item.color}`}>
              {progress[item.key] ?? 0}
            </WiniTypography>
            <WiniTypography variant="span" className="block text-xs text-text-sub">{item.label}</WiniTypography>
          </WiniBox>
        ))}
      </WiniBox>
      {participants.length > 0 && (
        <WiniTypography variant="span" className="mt-2 block text-sm text-text-sub">
          미확인 {progress.unconfirmedCount ?? 0}건 = 미참여 {notParticipated}명 · 부분완료 {partial}명
        </WiniTypography>
      )}
    </WiniBox>
  );
};
