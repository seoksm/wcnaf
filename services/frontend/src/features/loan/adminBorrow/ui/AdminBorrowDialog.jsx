import {
  WiniBox,
  WiniButton,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniMenuItem,
  WiniSelect,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';

/** S-412 대여 처리 (관리자 대행) - P-6 모달 */
export const AdminBorrowDialog = ({
  open, form, userList, isResolving, isSubmitting,
  onClose, onChange, onResolveAsset, onSubmit,
}) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <WiniDialogTitle>대여 처리 (관리자 대행)</WiniDialogTitle>
      <WiniDialogContent>
        <WiniBox className="flex flex-col gap-3 pt-2">
          <WiniBox className="flex items-end gap-2">
            <WiniText
              ui="column"
              label="자산코드"
              name="assetCode"
              className="w-full"
              value={form.assetCode}
              onChange={onChange}
              slotProps={{ inputLabel: { shrink: true } }}
            />
            <WiniButton ui="lineGray" onClick={onResolveAsset} loading={isResolving} disabled={isResolving}>
              조회
            </WiniButton>
          </WiniBox>
          {form.assetName ? (
            <WiniTypography variant="span" className="text-sm text-text-sub">
              대상: {form.assetName} ({form.assetCode})
            </WiniTypography>
          ) : null}

          <WiniSelect
            ui="column"
            label="대여자"
            name="memberId"
            className="w-full"
            value={form.memberId}
            displayEmpty
            onChange={onChange}
          >
            {userList.map((user) => (
              <WiniMenuItem value={user.id} key={user.id}>{user.fullName || user.username}</WiniMenuItem>
            ))}
          </WiniSelect>
        </WiniBox>
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="line" onClick={onSubmit} loading={isSubmitting} disabled={isSubmitting}>대여 처리</WiniButton>
        <WiniButton ui="lineGray" onClick={onClose}>취소</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
