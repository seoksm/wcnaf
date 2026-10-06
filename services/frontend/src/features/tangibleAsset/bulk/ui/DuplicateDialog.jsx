import {
  WiniBox,
  WiniButton,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniNumber,
  WiniTypography,
} from '@/shared/ui/wini';

/**
 * 유형자산 복제 (S-214)
 */
export const DuplicateDialog = ({
  open,
  sourceAsset,
  duplicateData,
  isSubmitting,
  onChange,
  onClose,
  onSubmit,
}) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <WiniDialogTitle>자산 복제</WiniDialogTitle>

      <WiniDialogContent className="pt-4 pb-4">
        <WiniBox className="flex flex-col gap-4">
          <WiniTypography variant="span" className="text-text-default">
            {sourceAsset?.assetName}
            {sourceAsset?.assetCode ? ` (${sourceAsset.assetCode})` : ''} 을(를)
            복제합니다. 복제본은 미배정 상태로 새 자산코드가 자동 채번됩니다.
          </WiniTypography>

          <WiniNumber
            ui="column"
            label="복제 개수"
            name="count"
            required
            value={duplicateData.count}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
            inputProps={{
              allowNegative: false,
              decimalScale: 0,
              inputMode: 'numeric',
            }}
          />
        </WiniBox>
      </WiniDialogContent>

      <WiniDialogActions>
        <WiniButton
          onClick={onSubmit}
          loading={isSubmitting}
          disabled={isSubmitting}
        >
          복제
        </WiniButton>
        <WiniButton ui="lineGray" onClick={onClose} disabled={isSubmitting}>
          취소
        </WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
