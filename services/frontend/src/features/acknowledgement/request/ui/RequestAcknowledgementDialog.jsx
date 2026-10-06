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
import { ACK_TYPE_LABEL } from '@/entities/ackTemplate';

/** S-430 확인서 요청 - P-6 모달 */
export const RequestAcknowledgementDialog = ({
  open, form, userList, isResolving, isSubmitting,
  onClose, onChange, onResolveAsset, onSubmit,
}) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <WiniDialogTitle>확인서 요청</WiniDialogTitle>
      <WiniDialogContent>
        <WiniBox className="flex flex-col gap-3 pt-2">
          <WiniSelect ui="column" label="구분" name="type" className="w-full" value={form.type} onChange={onChange}>
            {Object.entries(ACK_TYPE_LABEL).map(([value, label]) => (
              <WiniMenuItem value={value} key={value}>{label}확인서</WiniMenuItem>
            ))}
          </WiniSelect>

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
            <WiniButton ui="lineGray" onClick={onResolveAsset} loading={isResolving} disabled={isResolving}>조회</WiniButton>
          </WiniBox>
          {form.assetName ? (
            <WiniTypography variant="span" className="text-sm text-text-sub">
              대상: {form.assetName} ({form.assetCode})
            </WiniTypography>
          ) : null}

          <WiniSelect ui="column" label="대상자" name="memberId" className="w-full" value={form.memberId} displayEmpty onChange={onChange}>
            {userList.map((user) => (
              <WiniMenuItem value={user.id} key={user.id}>{user.fullName || user.username}</WiniMenuItem>
            ))}
          </WiniSelect>

          <WiniText
            ui="column"
            label="담당자 (문서에 표시될 이름, 선택)"
            name="managerName"
            className="w-full"
            value={form.managerName}
            onChange={onChange}
            slotProps={{ inputLabel: { shrink: true } }}
          />
        </WiniBox>
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="line" onClick={onSubmit} loading={isSubmitting} disabled={isSubmitting}>요청</WiniButton>
        <WiniButton ui="lineGray" onClick={onClose}>취소</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
