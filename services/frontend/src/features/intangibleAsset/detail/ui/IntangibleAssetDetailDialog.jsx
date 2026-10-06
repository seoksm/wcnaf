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
import { INTANGIBLE_TYPE_LABEL } from '@/entities/intangibleAsset';

const formatDateTime = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD HH:mm') : '-');
const formatDate = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD') : '-');

/** S-502 무형자산 상세 · 갱신 이력 */
export const IntangibleAssetDetailDialog = ({
  row, actionLog, isLoading, isActing, renewForm,
  onRenewFormChange, onSubmitRenew, onEdit, onDelete, onClose,
}) => {
  return (
    <WiniDialog open={!!row} onClose={onClose} fullWidth maxWidth="sm">
      <WiniDialogTitle>무형자산 상세</WiniDialogTitle>
      <WiniDialogContent>
        {row && (
          <WiniBox className="flex flex-col gap-4">
            <WiniBox>
              <WiniTypography variant="span" className="text-sm font-semibold">
                {INTANGIBLE_TYPE_LABEL[row.intangibleType]} · {row.name}
              </WiniTypography>
              <WiniTypography variant="span" className="block text-xs text-text-sub">
                발급기관: {row.issuer || '-'} · 만료일: {formatDate(row.expiryDate)}
                {row.expired ? ' (만료됨)' : row.nearExpiry ? ' (임박)' : ''}
              </WiniTypography>
            </WiniBox>

            <WiniBox className="flex flex-col gap-2 rounded border border-solid border-gray-200 p-3">
              <WiniTypography variant="span" className="text-sm font-semibold">갱신</WiniTypography>
              <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
                <WiniGridItem xs={8}>
                  <WiniDatePicker
                    ui="column"
                    label="새 만료일"
                    className="w-full"
                    value={renewForm.newExpiryDate || ''}
                    onChange={(event) => {
                      const raw = event?.value ?? event?.target?.value;
                      onRenewFormChange('newExpiryDate', raw ? winiDate.dateFormat(raw, 'YYYY-MM-DD') : '');
                    }}
                    slotProps={{ inputLabel: { shrink: true } }}
                  />
                </WiniGridItem>
                <WiniGridItem xs={4}>
                  <WiniButton ui="line" className="w-full" onClick={onSubmitRenew} loading={isActing} disabled={isActing}>갱신</WiniButton>
                </WiniGridItem>
                <WiniGridItem xs={12}>
                  <WiniText
                    ui="column"
                    label="메모"
                    className="w-full"
                    value={renewForm.note}
                    onChange={(e) => onRenewFormChange('note', e.target.value)}
                  />
                </WiniGridItem>
              </WiniGridLayout>
            </WiniBox>

            <WiniBox>
              <WiniTypography variant="span" className="mb-2 block text-sm font-semibold">갱신 이력</WiniTypography>
              {isLoading ? (
                <WiniTypography variant="span" className="text-xs text-text-sub">불러오는 중...</WiniTypography>
              ) : (
                <WiniBox className="flex flex-col gap-2">
                  {(actionLog || []).map((log) => (
                    <WiniBox key={log.intangibleAssetActionLogId} className="rounded border border-solid border-gray-200 p-2 text-xs">
                      <WiniTypography variant="span" className="block">
                        {formatDate(log.previousExpiryDate)} → {formatDate(log.newExpiryDate)}
                      </WiniTypography>
                      <WiniTypography variant="span" className="block text-text-sub">
                        {formatDateTime(log.actedAt)} {log.note ? `- ${log.note}` : ''}
                      </WiniTypography>
                    </WiniBox>
                  ))}
                  {(actionLog || []).length === 0 && (
                    <WiniTypography variant="span" className="text-xs text-text-sub">갱신 이력이 없습니다.</WiniTypography>
                  )}
                </WiniBox>
              )}
            </WiniBox>
          </WiniBox>
        )}
      </WiniDialogContent>
      <WiniDialogActions>
        {winiCom.checkMenuAut('delete', (
          <WiniButton ui="delete" onClick={onDelete} disabled={isActing}>삭제</WiniButton>
        ))}
        {winiCom.checkMenuAut('update', (
          <WiniButton ui="lineGray" onClick={() => onEdit(row)}>수정</WiniButton>
        ))}
        <WiniButton ui="lineGray" onClick={onClose}>닫기</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
