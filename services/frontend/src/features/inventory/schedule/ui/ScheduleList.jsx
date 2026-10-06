import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { winiCom, winiDate } from '@/shared/lib';
import { INVENTORY_TYPE_LABEL, RECURRENCE_RULE_LABEL } from '@/entities/inventory';

const formatDate = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD') : '-');

/** S-306 반복 시행 스케줄 목록 - 유형별 1행, "템플릿으로 새로 만들기"가 기본 동선(§4) */
export const ScheduleList = ({ schedules, isLoading, error, isCloning, onClone }) => {
  if (isLoading) {
    return <WiniTypography variant="span" className="text-sm text-text-sub">불러오는 중...</WiniTypography>;
  }

  if (error) {
    return <WiniTypography variant="span" className="text-sm text-red-500">{error}</WiniTypography>;
  }

  if (!schedules || schedules.length === 0) {
    return (
      <WiniTypography variant="span" className="text-sm text-gray-400">
        반복 주기를 설정하고 종료된 조사가 아직 없습니다.
      </WiniTypography>
    );
  }

  return (
    <WiniBox className="flex flex-col gap-2">
      {schedules.map((s) => (
        <WiniBox
          key={s.inventoryType}
          className="flex items-center justify-between rounded border border-solid border-gray-200 bg-white px-3 py-2"
        >
          <WiniBox className="flex flex-col">
            <WiniTypography variant="span" className="text-sm font-semibold">
              {INVENTORY_TYPE_LABEL[s.inventoryType] || s.inventoryType}
              {' · '}
              {RECURRENCE_RULE_LABEL[s.recurrenceRule] || s.recurrenceRule}
            </WiniTypography>
            <WiniTypography variant="span" className="text-xs text-text-sub">
              직전 종료: {s.lastClosedTitle} ({formatDate(s.lastClosedAt)})
            </WiniTypography>
            <WiniTypography variant="span" className={`text-xs ${s.overdue ? 'text-red-600 font-semibold' : 'text-text-sub'}`}>
              {s.nextCycleInProgress
                ? '다음 회차가 이미 진행 중입니다.'
                : `다음 예정일: ${formatDate(s.nextDueDate)}${s.overdue ? ' (지남)' : ''}`}
            </WiniTypography>
          </WiniBox>
          {!s.nextCycleInProgress &&
            winiCom.checkMenuAut(
              'insert',
              <WiniButton ui="lineGray" onClick={() => onClone(s)} loading={isCloning} disabled={isCloning}>
                템플릿으로 새로 만들기
              </WiniButton>,
            )}
        </WiniBox>
      ))}
    </WiniBox>
  );
};
