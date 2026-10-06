import { WiniBox, WiniButton, WiniDialog, WiniDialogActions, WiniDialogContent, WiniDialogTitle, WiniTypography } from '@/shared/ui/wini';

/** L3 - 반납 시 자산 상태 확인(정상/이상)을 강제한다 */
export const ReturnConditionDialog = ({ pendingReturn, isActing, onCancel, onConfirm }) => {
  return (
    <WiniDialog open={!!pendingReturn} onClose={onCancel} fullWidth maxWidth="xs">
      <WiniDialogTitle>반납 - 자산 상태 확인</WiniDialogTitle>
      <WiniDialogContent>
        <WiniBox className="flex flex-col gap-2 pt-2">
          <WiniTypography variant="span" className="text-sm text-text-sub">
            {pendingReturn ? `${pendingReturn.assetName}(${pendingReturn.assetCode})` : ''} - 반납 전 상태를 확인해주세요.
          </WiniTypography>
        </WiniBox>
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="line" onClick={() => onConfirm(false)} disabled={isActing} loading={isActing}>정상 반납</WiniButton>
        <WiniButton ui="delete" onClick={() => onConfirm(true)} disabled={isActing}>이상 있음</WiniButton>
        <WiniButton ui="lineGray" onClick={onCancel} disabled={isActing}>취소</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
