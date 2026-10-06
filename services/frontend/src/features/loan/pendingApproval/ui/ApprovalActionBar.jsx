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
import { winiCom } from '@/shared/lib';

export const ApprovalActionBar = ({
  selectedRow, isActing, onApprove, onOpenReject,
  rejectDialogOpen, rejectReason, onRejectReasonChange, onCloseReject, onSubmitReject,
}) => {
  return (
    <>
      <WiniBox className="mt-2 flex items-center justify-between">
        <WiniTypography variant="span" className="text-text-sub text-sm">
          {selectedRow ? `선택됨: ${selectedRow.assetName}(${selectedRow.assetCode})` : '승인/반려할 항목을 선택하세요.'}
        </WiniTypography>
        <WiniBox ui="btnbox" className="mt-0">
          <WiniBox ui="btnitem">
            {winiCom.checkMenuAut(
              'update',
              <WiniButton ui="lineGray" className="w-20" onClick={onOpenReject} disabled={!selectedRow || isActing}>
                반려
              </WiniButton>,
            )}
            {winiCom.checkMenuAut(
              'update',
              <WiniButton ui="line" className="w-20" onClick={onApprove} disabled={!selectedRow || isActing} loading={isActing}>
                승인
              </WiniButton>,
            )}
          </WiniBox>
        </WiniBox>
      </WiniBox>

      <WiniDialog open={rejectDialogOpen} onClose={onCloseReject} fullWidth maxWidth="xs">
        <WiniDialogTitle>대여 반려</WiniDialogTitle>
        <WiniDialogContent>
          <WiniText
            ui="column"
            label="반려 사유"
            required
            multiline
            minRows={3}
            className="w-full pt-2"
            value={rejectReason}
            onChange={(e) => onRejectReasonChange(e.target.value)}
          />
        </WiniDialogContent>
        <WiniDialogActions>
          <WiniButton ui="line" onClick={onSubmitReject} loading={isActing} disabled={isActing}>반려</WiniButton>
          <WiniButton ui="lineGray" onClick={onCloseReject}>취소</WiniButton>
        </WiniDialogActions>
      </WiniDialog>
    </>
  );
};
