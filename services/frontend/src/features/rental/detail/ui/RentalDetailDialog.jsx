import {
  WiniBox,
  WiniButton,
  WiniDatePicker,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniGridItem,
  WiniGridLayout,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';
import { winiCom, winiDate } from '@/shared/lib';
import { BILLING_MODE_LABEL, RENTAL_STATUS_LABEL } from '@/entities/rental';

const formatDate = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD') : '-');
const formatAmount = (value) => (value == null ? '-' : Number(value).toLocaleString());

/** S-512 렌탈·구독 상세 - 기본정보 + 결제 스케줄(귀속월 ≠ 결제일, Q-53 예상·실제 분리) */
export const RentalDetailDialog = ({
  row, schedules, isLoading, isActing, newSchedule, actualAmountDraft,
  onNewScheduleChange, onNewScheduleDateChange, onAddSchedule, onActualAmountChange, onConfirmSchedule,
  onEdit, onCancel, onClose,
}) => {
  const cancellable = row?.status === 'ACTIVE';

  return (
    <WiniDialog open={!!row} onClose={onClose} fullWidth maxWidth="md">
      <WiniDialogTitle>렌탈·구독 상세</WiniDialogTitle>
      <WiniDialogContent>
        {row && (
          <WiniBox className="flex flex-col gap-4">
            <WiniBox className="flex items-center justify-between">
              <WiniBox>
                <WiniTypography variant="span" className="text-sm font-semibold">{row.name}</WiniTypography>
                <WiniTypography variant="span" className="block text-xs text-text-sub">
                  {BILLING_MODE_LABEL[row.billingMode]} · {formatDate(row.startDate)} ~ {formatDate(row.endDate) === '-' ? '(진행중)' : formatDate(row.endDate)}
                </WiniTypography>
              </WiniBox>
              <span className="rounded border border-solid border-gray-300 px-2 py-0.5 text-xs">{RENTAL_STATUS_LABEL[row.status]}</span>
            </WiniBox>

            <WiniBox>
              <WiniTypography variant="span" className="mb-2 block text-sm font-semibold">결제 스케줄</WiniTypography>
              <WiniGridLayout container columnSpacing={2} rowSpacing={1} className="mb-2 items-end">
                <WiniGridItem size={{ xs: 4 }}>
                  <WiniDatePicker ui="column" label="귀속월" className="w-full" value={newSchedule.accrualMonth || ''} onChange={onNewScheduleDateChange('accrualMonth')} slotProps={{ inputLabel: { shrink: true } }} />
                </WiniGridItem>
                <WiniGridItem size={{ xs: 4 }}>
                  <WiniDatePicker ui="column" label="결제일" className="w-full" value={newSchedule.dueDate || ''} onChange={onNewScheduleDateChange('dueDate')} slotProps={{ inputLabel: { shrink: true } }} />
                </WiniGridItem>
                <WiniGridItem size={{ xs: 2 }}>
                  <WiniText ui="column" label="예상금액" className="w-full" value={newSchedule.expectedAmount} onChange={(e) => onNewScheduleChange('expectedAmount', e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
                </WiniGridItem>
                <WiniGridItem size={{ xs: 2 }}>
                  <WiniButton ui="lineGray" className="w-full" onClick={onAddSchedule} disabled={isActing}>추가</WiniButton>
                </WiniGridItem>
              </WiniGridLayout>

              {isLoading ? (
                <WiniTypography variant="span" className="text-xs text-text-sub">불러오는 중...</WiniTypography>
              ) : (
                <WiniBox className="flex flex-col gap-2">
                  {(schedules || []).map((s) => (
                    <WiniBox key={s.paymentScheduleId} className="flex flex-wrap items-center gap-3 rounded border border-solid border-gray-200 p-2 text-xs">
                      <span className="font-semibold">{formatDate(s.accrualMonth)}</span>
                      <span className="text-text-sub">결제일 {formatDate(s.dueDate)}</span>
                      <span className="text-text-sub">예상 {formatAmount(s.expectedAmount)}원</span>
                      {s.confirmedYn ? (
                        <span className="font-semibold text-green-600">실제 {formatAmount(s.actualAmount)}원 (확정)</span>
                      ) : (
                        <WiniBox className="flex items-center gap-1">
                          <WiniText
                            className="w-28"
                            value={actualAmountDraft[s.paymentScheduleId] || ''}
                            onChange={(e) => onActualAmountChange(s.paymentScheduleId, e.target.value)}
                            placeholder="실제금액"
                          />
                          <WiniButton ui="lineGray" onClick={() => onConfirmSchedule(s.paymentScheduleId)} disabled={isActing}>확정</WiniButton>
                        </WiniBox>
                      )}
                    </WiniBox>
                  ))}
                  {(schedules || []).length === 0 && (
                    <WiniTypography variant="span" className="text-xs text-text-sub">등록된 결제 스케줄이 없습니다.</WiniTypography>
                  )}
                </WiniBox>
              )}
            </WiniBox>
          </WiniBox>
        )}
      </WiniDialogContent>
      <WiniDialogActions>
        {cancellable && winiCom.checkMenuAut('update', (
          <WiniButton ui="delete" onClick={onCancel} disabled={isActing}>해지</WiniButton>
        ))}
        {winiCom.checkMenuAut('update', (
          <WiniButton ui="lineGray" onClick={() => onEdit(row)}>수정</WiniButton>
        ))}
        <WiniButton ui="lineGray" onClick={onClose}>닫기</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
