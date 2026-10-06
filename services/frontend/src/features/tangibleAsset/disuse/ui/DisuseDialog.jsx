import {
  WiniBox,
  WiniButton,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';

/**
 * 불용 처리 (S-241) - 파괴적 동작(패턴 P-6). 사유는 이력에만 남기고 자산 메모는 건드리지 않는다.
 */
export const DisuseDialog = ({ open, targetAsset, reason, isSubmitting, onChangeReason, onClose, onSubmit }) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <WiniDialogTitle>불용 처리</WiniDialogTitle>

      <WiniDialogContent className="pt-4 pb-4">
        <WiniBox className="flex flex-col gap-4">
          <WiniTypography variant="span" className="text-text-default">
            {targetAsset?.assetName}
            {targetAsset?.assetCode ? ` (${targetAsset.assetCode})` : ''} 을(를) 불용 처리합니다. 배정되어
            있다면 자동으로 해제되며, 불용 처리한 달까지는 상각하고 다음 달부터 상각이 멈춥니다.
          </WiniTypography>

          <WiniText
            ui="column"
            label="불용 사유"
            name="reason"
            multiline
            minRows={2}
            value={reason || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChangeReason}
          />
        </WiniBox>
      </WiniDialogContent>

      <WiniDialogActions>
        <WiniButton onClick={onSubmit} loading={isSubmitting} disabled={isSubmitting}>불용 처리</WiniButton>
        <WiniButton ui="lineGray" onClick={onClose} disabled={isSubmitting}>
          취소
        </WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
