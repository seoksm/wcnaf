import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { LOAN_STATUS_LABEL } from '@/entities/loan';

const STATUS_CHIP_CLASS = {
  PENDING_APPROVAL: 'border-blue-300 text-blue-600',
  ACTIVE: 'border-green-300 text-green-600',
  RETURNED: 'border-gray-300 text-gray-500',
  REJECTED: 'border-red-300 text-red-600',
};

/** 연체 건을 최상단에, 그다음은 최근 대여일시 순으로 (설계문서 §3 "연체 우선") */
const sortForDisplay = (loans) => {
  return [...(loans || [])].sort((a, b) => {
    if (a.overdue !== b.overdue) return a.overdue ? -1 : 1;
    return winiDate(b.borrowedAt).valueOf() - winiDate(a.borrowedAt).valueOf();
  });
};

export const MyLoanList = ({ loans, isActing, onStartReturnScan, onExtend }) => {
  const activeCount = (loans || []).filter((l) => l.status === 'ACTIVE').length;
  const sorted = sortForDisplay(loans);

  return (
    <WiniBox className="flex flex-col gap-3">
      <WiniBox className="flex items-center justify-between">
        <WiniTypography variant="h2">내 대여 자산</WiniTypography>
        <WiniButton onClick={onStartReturnScan} disabled={activeCount === 0}>QR 스캔으로 반납</WiniButton>
      </WiniBox>

      <WiniBox className="flex flex-col gap-2">
        {sorted.map((loan) => (
          <WiniBox
            key={loan.loanId}
            className={`flex flex-col gap-1 rounded border border-solid bg-white p-3 ${
              loan.overdue ? 'border-red-300' : 'border-gray-200'
            }`}
          >
            <WiniBox className="flex items-center justify-between">
              <WiniTypography variant="span" className="text-sm font-semibold">
                {loan.assetName} ({loan.assetCode})
              </WiniTypography>
              <span className={`rounded border border-solid px-2 py-0.5 text-xs font-semibold ${STATUS_CHIP_CLASS[loan.status] || 'border-gray-300 text-gray-500'}`}>
                {LOAN_STATUS_LABEL[loan.status] || loan.status}
              </span>
            </WiniBox>
            <WiniTypography variant="span" className="text-xs text-text-sub">{loan.categoryName}</WiniTypography>

            {loan.status === 'ACTIVE' && (
              <>
                <WiniTypography variant="span" className={`text-xs font-semibold ${loan.overdue ? 'text-red-600' : 'text-text-sub'}`}>
                  반납기한 {winiDate.dateFormat(winiDate(loan.dueDate), 'YYYY-MM-DD')}
                  {loan.overdue ? ' - 연체' : ''}
                </WiniTypography>
                <WiniBox className="flex gap-2">
                  <WiniButton ui="lineGray" onClick={() => onExtend(loan.loanId)} disabled={isActing}>연장 신청</WiniButton>
                </WiniBox>
              </>
            )}
          </WiniBox>
        ))}
        {(loans || []).length === 0 && (
          <WiniBox ui="info" className="p-4 text-center">
            <WiniTypography variant="span" className="text-text-sub">대여 이력이 없습니다.</WiniTypography>
          </WiniBox>
        )}
      </WiniBox>
    </WiniBox>
  );
};
