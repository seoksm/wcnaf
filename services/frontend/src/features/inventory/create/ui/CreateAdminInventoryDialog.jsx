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

/** S-302 전수조사 생성(관리자형) - 대상은 공용·미배정 자산, 특정 자산이 아니라 검수자 풀 전체가 검수(Q-32) */
export const CreateAdminInventoryDialog = ({
  open,
  formData,
  userList,
  preview,
  isPreviewing,
  isSubmitting,
  onChange,
  onToggle,
  onClose,
  onSubmit,
}) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <WiniDialogTitle>전수조사 생성 (관리자형)</WiniDialogTitle>

      <WiniDialogContent className="pt-4 pb-4">
        <WiniBox className="flex flex-col gap-3">
          <WiniTypography variant="span" className="text-sm text-text-sub">
            공용·미배정 자산이 대상입니다. 특정 자산에 검수자를 지정하지 않고, 아래 검수자 풀 전체가
            대상 자산 전체를 검수할 수 있습니다.
            {isPreviewing ? ' 대상 자산 수 계산 중...' : preview ? ` 현재 대상 자산 ${preview.targetAssetCount}건.` : ''}
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
            label="검수자"
            name="inspectorMemberIds"
            required
            multiple
            value={formData.inspectorMemberIds}
            displayEmpty
            renderValue={(selected) =>
              selected.length === 0
                ? '선택 안 함'
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
        </WiniBox>
      </WiniDialogContent>

      <WiniDialogActions>
        <WiniButton onClick={onSubmit} loading={isSubmitting} disabled={isSubmitting}>생성</WiniButton>
        <WiniButton ui="lineGray" onClick={onClose} disabled={isSubmitting}>취소</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
