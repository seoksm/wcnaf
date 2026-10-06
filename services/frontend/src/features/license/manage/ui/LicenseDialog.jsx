import {
  WiniBox,
  WiniButton,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniText,
} from '@/shared/ui/wini';

/** S-531 라이선스 등록·수정 - P-6 모달 */
export const LicenseDialog = ({ open, form, isSubmitting, onClose, onChange, onSubmit }) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <WiniDialogTitle>{form.licenseId ? '라이선스 수정' : '라이선스 등록'}</WiniDialogTitle>
      <WiniDialogContent>
        <WiniBox className="flex flex-col gap-3 pt-2">
          <WiniText ui="column" label="라이선스명" name="name" required className="w-full" value={form.name} onChange={onChange} slotProps={{ inputLabel: { shrink: true } }} />
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
