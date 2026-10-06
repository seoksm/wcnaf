import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';

export const MyLicenseList = ({ licenses, isActing, onRequestRelease }) => {
  return (
    <WiniBox className="flex flex-col gap-3">
      <WiniTypography variant="h2">내 라이선스</WiniTypography>

      <WiniBox className="flex flex-col gap-2">
        {(licenses || []).map((l) => (
          <WiniBox key={l.licenseAssignedUserId} className="flex flex-col gap-1 rounded border border-solid border-gray-200 bg-white p-3">
            <WiniBox className="flex items-center justify-between">
              <WiniTypography variant="span" className="text-sm font-semibold">{l.licenseName}</WiniTypography>
              {l.releaseRequestedYn && (
                <span className="rounded border border-solid border-blue-300 px-2 py-0.5 text-xs font-semibold text-blue-600">회수 요청됨</span>
              )}
            </WiniBox>
            <WiniTypography variant="span" className="text-xs text-text-sub">
              배정일 {winiDate.dateFormat(winiDate(l.assignedAt), 'YYYY-MM-DD')}
            </WiniTypography>
            {l.unlinkedPurchase && (
              <WiniTypography variant="span" className="text-xs text-orange-600">미연결 배정</WiniTypography>
            )}
            <WiniBox className="flex gap-2">
              <WiniButton
                ui="lineGray"
                onClick={() => onRequestRelease(l.licenseAssignedUserId)}
                disabled={isActing || l.releaseRequestedYn}
              >
                {l.releaseRequestedYn ? '회수 요청됨' : '회수 요청'}
              </WiniButton>
            </WiniBox>
          </WiniBox>
        ))}
        {(licenses || []).length === 0 && (
          <WiniBox ui="info" className="p-4 text-center">
            <WiniTypography variant="span" className="text-text-sub">배정된 라이선스가 없습니다.</WiniTypography>
          </WiniBox>
        )}
      </WiniBox>
    </WiniBox>
  );
};
