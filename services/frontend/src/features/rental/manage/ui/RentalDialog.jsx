import {
  WiniBox,
  WiniButton,
  WiniDatePicker,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniMenuItem,
  WiniSelect,
  WiniText,
} from '@/shared/ui/wini';
import { BILLING_MODE_LABEL } from '@/entities/rental';

/** S-511 렌탈·구독 등록·수정 - P-6 모달 */
export const RentalDialog = ({ open, form, isSubmitting, onClose, onChange, onDateChange, onSubmit }) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <WiniDialogTitle>{form.rentalAssetId ? '렌탈·구독 수정' : '렌탈·구독 등록'}</WiniDialogTitle>
      <WiniDialogContent>
        <WiniBox className="flex flex-col gap-3 pt-2">
          <WiniText ui="column" label="렌탈·구독명" name="name" required className="w-full" value={form.name} onChange={onChange} slotProps={{ inputLabel: { shrink: true } }} />
          <WiniSelect ui="column" label="결제방식" name="billingMode" className="w-full" value={form.billingMode} onChange={onChange}>
            {Object.entries(BILLING_MODE_LABEL).map(([value, label]) => (
              <WiniMenuItem value={value} key={value}>{label}</WiniMenuItem>
            ))}
          </WiniSelect>
          <WiniDatePicker ui="column" label="시작일" required className="w-full" value={form.startDate || ''} onChange={onDateChange('startDate')} slotProps={{ inputLabel: { shrink: true } }} />
          <WiniDatePicker ui="column" label="종료일" className="w-full" value={form.endDate || ''} onChange={onDateChange('endDate')} slotProps={{ inputLabel: { shrink: true } }} />
          <WiniText ui="column" label="메모" name="memo" multiline minRows={2} className="w-full" value={form.memo} onChange={onChange} />
        </WiniBox>
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="line" onClick={onSubmit} loading={isSubmitting} disabled={isSubmitting}>저장</WiniButton>
        <WiniButton ui="lineGray" onClick={onClose}>취소</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
