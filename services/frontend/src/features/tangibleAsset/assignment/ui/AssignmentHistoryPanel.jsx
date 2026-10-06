import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { winiCom, winiDate } from '@/shared/lib';
import { ASSIGN_TYPE_LABEL } from '@/entities/tangibleAsset';

const shortenId = (id) => (id ? `${id.slice(0, 8)}…` : '-');

const findMemberName = (userList, memberId) => {
  const user = (userList || []).find((u) => u.id === memberId);
  if (user) return user.fullName || user.username;
  return memberId ? `알 수 없음 (${shortenId(memberId)})` : '-';
};

const formatDateTime = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD HH:mm') : '-');

// ASSIGN_TYPE_LABEL의 "대여중"은 "PERSONAL → 개인배정"과 달리 그 자체로 "지금 진행 중"이라는
// 시제를 담고 있어서, 반납이 끝나 releasedAt이 찍힌 이력 행에도 그대로 쓰면 "반납했는데 왜 아직
// 대여중이라고 나오나"로 헷갈린다. 이 이력 패널에서만 종료된 대여 건은 별도 라벨로 바꿔준다.
const resolveAssignTypeLabel = (item) => {
  if (item.assignType === 'ON_LOAN' && item.releasedAt) return '대여완료';
  return ASSIGN_TYPE_LABEL[item.assignType] || item.assignType;
};

/**
 * 유형자산 배정 이력 · 회수 (S-218)
 * 좁은 화면에서도 깨지지 않도록 표 형태 대신 항목별 카드 목록으로 표시한다.
 */
export const AssignmentHistoryPanel = ({ history, userList, onRelease, isLoading, isReleasing }) => {
  const current = (history || []).find((h) => !h.releasedAt);

  return (
    <WiniBox ui="info" className="p-4">
      <WiniBox className="mb-3 flex items-center justify-between">
        <WiniTypography variant="span" className="text-sm font-bold text-text-main">배정 이력</WiniTypography>
        {current &&
          winiCom.checkMenuAut(
            'update',
            <WiniButton ui="line" className="w-20" onClick={onRelease} loading={isReleasing} disabled={isReleasing}>
              회수
            </WiniButton>,
          )}
      </WiniBox>

      {isLoading && <WiniTypography variant="span" className="block py-2 text-sm text-gray-400">불러오는 중...</WiniTypography>}
      {!isLoading && (!history || history.length === 0) && (
        <WiniTypography variant="span" className="block py-2 text-sm text-gray-400">배정 이력이 없습니다.</WiniTypography>
      )}

      <WiniBox className="flex flex-col gap-2">
        {(history || []).map((item) => (
          <WiniBox
            key={item.assetAssignmentId}
            className="rounded border border-solid border-gray-200 bg-white px-3 py-2"
          >
            <WiniBox className="flex items-center justify-between gap-2">
              <WiniTypography variant="span" className="text-sm font-semibold">{resolveAssignTypeLabel(item)}</WiniTypography>
              <WiniTypography variant="span" className="truncate text-sm text-text-sub">{findMemberName(userList, item.memberId)}</WiniTypography>
            </WiniBox>
            <WiniTypography variant="span" className="mt-1 block text-xs text-gray-400">
              {formatDateTime(item.assignedAt)}
              {item.releasedAt ? ` → ${formatDateTime(item.releasedAt)}` : ' (현재 배정중)'}
            </WiniTypography>
          </WiniBox>
        ))}
      </WiniBox>
    </WiniBox>
  );
};
