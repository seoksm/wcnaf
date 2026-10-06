import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { ACK_TYPE_LABEL } from '@/entities/ackTemplate';

/** S-440 확인서 승인 목록 - 임직원 본인의 승인대기 건만 노출 */
export const MyAcknowledgementList = ({ list, onSelect }) => {
  return (
    <WiniBox className="flex flex-col gap-3">
      <WiniTypography variant="h2">확인서 승인 대기</WiniTypography>

      <WiniBox className="flex flex-col gap-2">
        {(list || []).map((row) => (
          <WiniBox
            key={row.acknowledgementId}
            className="flex cursor-pointer flex-col gap-1 rounded border border-solid border-gray-200 bg-white p-3"
            onClick={() => onSelect(row)}
          >
            <WiniBox className="flex items-center justify-between">
              <WiniTypography variant="span" className="text-sm font-semibold">
                {row.assetName} ({row.assetCode})
              </WiniTypography>
              <span className="rounded border border-solid border-blue-300 px-2 py-0.5 text-xs font-semibold text-blue-600">
                {ACK_TYPE_LABEL[row.type]}확인서
              </span>
            </WiniBox>
            {row.dueDate && (
              <WiniTypography variant="span" className={`text-xs ${row.overdue ? 'font-semibold text-red-600' : 'text-text-sub'}`}>
                승인기한 {winiDate.dateFormat(winiDate(row.dueDate), 'YYYY-MM-DD')}{row.overdue ? ' - 기한초과' : ''}
              </WiniTypography>
            )}
          </WiniBox>
        ))}
        {(list || []).length === 0 && (
          <WiniBox ui="info" className="p-4 text-center">
            <WiniTypography variant="span" className="text-text-sub">승인 대기 중인 확인서가 없습니다.</WiniTypography>
          </WiniBox>
        )}
      </WiniBox>
    </WiniBox>
  );
};
