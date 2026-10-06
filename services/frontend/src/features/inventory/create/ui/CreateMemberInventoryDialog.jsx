import {
  WiniBox,
  WiniButton,
  WiniCheckbox,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniMenuItem,
  WiniSelect,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';
import { RECURRENCE_RULE_LABEL } from '@/entities/inventory';

/** S-301 전수조사 생성(임직원형) - 대상은 개인배정 자산, 생성 전 대상 미리보기가 마지막 확인 지점 */
export const CreateMemberInventoryDialog = ({
  open,
  formData,
  userList,
  preview,
  isPreviewing,
  isSubmitting,
  onChange,
  onToggle,
  onRunPreview,
  onClose,
  onSubmit,
}) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <WiniDialogTitle>전수조사 생성 (임직원형)</WiniDialogTitle>

      <WiniDialogContent className="pt-4 pb-4">
        <WiniBox className="flex flex-col gap-3">
          <WiniTypography variant="span" className="text-sm text-text-sub">
            개인배정 자산만 대상으로 합니다. 공용·미배정 자산은 관리자형으로 별도 생성해주세요.
          </WiniTypography>

          <WiniText
            ui="column"
            label="조사명"
            name="title"
            required
            value={formData.title}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
          />

          <WiniSelect
            ui="column"
            label="반복 주기"
            name="recurrenceRule"
            value={formData.recurrenceRule}
            displayEmpty
            className="w-full"
            onChange={onChange}
          >
            <WiniMenuItem value="">사용 안 함</WiniMenuItem>
            {Object.entries(RECURRENCE_RULE_LABEL).map(([value, label]) => (
              <WiniMenuItem value={value} key={value}>{label}</WiniMenuItem>
            ))}
          </WiniSelect>

          <WiniBox className="flex items-center gap-2">
            <WiniCheckbox checked={formData.approvalRequired} onChange={onToggle('approvalRequired')} />
            <WiniTypography variant="span" className="text-sm">검수자 승인 단계 사용 (확인 → 승인 2단계)</WiniTypography>
          </WiniBox>

          <WiniBox className="flex items-center gap-2">
            <WiniCheckbox checked={formData.allowNewAssetRegistration} onChange={onToggle('allowNewAssetRegistration')} />
            <WiniTypography variant="span" className="text-sm">실사 중 미등록 자산의 신규 등록 허용</WiniTypography>
          </WiniBox>

          <WiniSelect
            ui="column"
            label="제외 임직원"
            name="excludedMemberIds"
            multiple
            value={formData.excludedMemberIds}
            displayEmpty
            renderValue={(selected) =>
              selected.length === 0
                ? '없음'
                : selected
                    .map((id) => userList.find((u) => u.id === id)?.fullName || id)
                    .join(', ')
            }
            className="w-full"
            onChange={onChange}
          >
            {userList.map((u) => (
              <WiniMenuItem value={u.id} key={u.id}>{u.fullName || u.username}</WiniMenuItem>
            ))}
          </WiniSelect>

          <WiniBox className="flex items-center justify-between rounded border border-solid border-gray-200 bg-white px-3 py-2">
            <WiniBox>
              {preview ? (
                <WiniTypography variant="span" className="text-sm">
                  대상 자산 <b>{preview.targetAssetCount}</b>건 · 참여자 <b>{preview.participantCount}</b>명
                  {preview.carriedOverTangibleAssetIds?.length > 0 &&
                    ` (이월 대상 ${preview.carriedOverTangibleAssetIds.length}건 포함)`}
                </WiniTypography>
              ) : (
                <WiniTypography variant="span" className="text-sm text-gray-400">
                  미리보기를 실행하면 대상 자산·참여자 수가 표시됩니다.
                </WiniTypography>
              )}
            </WiniBox>
            <WiniButton ui="lineGray" onClick={onRunPreview} loading={isPreviewing} disabled={isPreviewing}>
              미리보기
            </WiniButton>
          </WiniBox>
        </WiniBox>
      </WiniDialogContent>

      <WiniDialogActions>
        <WiniButton onClick={onSubmit} loading={isSubmitting} disabled={isSubmitting}>생성</WiniButton>
        <WiniButton ui="lineGray" onClick={onClose} disabled={isSubmitting}>취소</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
