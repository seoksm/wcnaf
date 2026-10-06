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
import { TICKET_TYPE_LABEL } from '@/entities/ticket';

/** S-610 티켓 등록 (임직원) - P-6 모달 */
export const MyTicketRegisterDialog = ({
  open, form, isResolving, isActing,
  onClose, onChange, onResolveAsset, onSubmit,
}) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <WiniDialogTitle>새 티켓 등록</WiniDialogTitle>
      <WiniDialogContent>
        <WiniBox className="flex flex-col gap-3 pt-2">
          <WiniSelect ui="column" label="유형" name="ticketType" className="w-full" value={form.ticketType} onChange={onChange}>
            {Object.entries(TICKET_TYPE_LABEL).map(([value, label]) => (
              <WiniMenuItem value={value} key={value}>{label}</WiniMenuItem>
            ))}
          </WiniSelect>
          <WiniText ui="column" label="제목" name="title" required className="w-full" value={form.title} onChange={onChange} slotProps={{ inputLabel: { shrink: true } }} />
          <WiniText ui="column" label="내용" name="content" multiline minRows={2} className="w-full" value={form.content} onChange={onChange} />

          <WiniBox className="flex items-end gap-2">
            <WiniText ui="column" label="연결 자산코드(선택)" name="assetCode" className="w-full" value={form.assetCode} onChange={onChange} slotProps={{ inputLabel: { shrink: true } }} />
            <WiniButton ui="lineGray" onClick={onResolveAsset} loading={isResolving} disabled={isResolving}>조회</WiniButton>
          </WiniBox>
          {form.assetName ? (
            <WiniTypography variant="span" className="text-sm text-text-sub">대상: {form.assetName} ({form.assetCode})</WiniTypography>
          ) : null}
        </WiniBox>
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="line" onClick={onSubmit} loading={isActing} disabled={isActing}>등록</WiniButton>
        <WiniButton ui="lineGray" onClick={onClose}>취소</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
