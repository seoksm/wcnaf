import {
  WiniBox,
  WiniButton,
  WiniCheckbox,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniSelect,
  WiniMenuItem,
  WiniTypography,
} from '@/shared/ui/wini';
import { EnumSelect } from '@/shared/ui/enum-select';
import {
  LIFE_STATUS_LABEL,
  ASSIGN_TYPE_LABEL,
  requiresMember,
} from '@/entities/tangibleAsset';

/**
 * 유형자산 일괄 변경 (S-215) - 체크한 항목만 적용
 */
export const BatchUpdateDialog = ({
  open,
  checkedCount,
  batchData,
  categoryList,
  locationList,
  userList,
  onChange,
  onToggle,
  onClose,
  onSubmit,
  isSubmitting,
  isOptionsUnavailable,
}) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <WiniDialogTitle>일괄 변경</WiniDialogTitle>

      <WiniDialogContent className="pt-4 pb-4">
        <WiniBox className="flex flex-col gap-4">
          <WiniTypography variant="span" className="text-text-default">
            선택한 {checkedCount}건에 대해 체크한 항목만 일괄 변경됩니다.
          </WiniTypography>

          <WiniBox className="flex items-center gap-2">
            <WiniCheckbox
              label="변경"
              size="large"
              checked={batchData.enableCategory}
              onChange={onToggle('enableCategory')}
              disabled={isSubmitting || isOptionsUnavailable}
            />
            <WiniSelect
              label="자산 종류"
              name="categoryId"
              value={batchData.categoryId}
              labelProps={{ shrink: true }}
              displayEmpty
              disabled={
                !batchData.enableCategory ||
                isSubmitting ||
                isOptionsUnavailable
              }
              className="w-full"
              onChange={onChange}
            >
              {(categoryList || []).map((c) => (
                <WiniMenuItem value={c.categoryId} key={c.categoryId}>
                  {c.categoryName}
                </WiniMenuItem>
              ))}
            </WiniSelect>
          </WiniBox>

          <WiniBox className="flex items-center gap-2">
            <WiniCheckbox
              label="변경"
              size="large"
              checked={batchData.enableLocation}
              onChange={onToggle('enableLocation')}
              disabled={isSubmitting || isOptionsUnavailable}
            />
            <WiniSelect
              label="자산 위치"
              name="locationId"
              value={batchData.locationId}
              labelProps={{ shrink: true }}
              displayEmpty
              disabled={
                !batchData.enableLocation ||
                isSubmitting ||
                isOptionsUnavailable
              }
              className="w-full"
              onChange={onChange}
            >
              {(locationList || []).map((l) => (
                <WiniMenuItem value={l.locationId} key={l.locationId}>
                  {l.locationName}
                </WiniMenuItem>
              ))}
            </WiniSelect>
          </WiniBox>

          <WiniBox className="flex items-center gap-2">
            <WiniCheckbox
              label="변경"
              size="large"
              checked={batchData.enableLifeStatus}
              onChange={onToggle('enableLifeStatus')}
              disabled={isSubmitting || isOptionsUnavailable}
            />
            <EnumSelect
              label="생애 상태"
              name="lifeStatus"
              value={batchData.lifeStatus}
              enums={LIFE_STATUS_LABEL}
              disabled={
                !batchData.enableLifeStatus ||
                isSubmitting ||
                isOptionsUnavailable
              }
              onChange={onChange}
            />
          </WiniBox>

          <WiniBox className="flex items-center gap-2">
            <WiniCheckbox
              label="변경"
              size="large"
              checked={batchData.enableAssignType}
              onChange={onToggle('enableAssignType')}
              disabled={isSubmitting || isOptionsUnavailable}
            />
            <EnumSelect
              label="배정 형태"
              name="assignType"
              value={batchData.assignType}
              enums={ASSIGN_TYPE_LABEL}
              disabled={
                !batchData.enableAssignType ||
                isSubmitting ||
                isOptionsUnavailable
              }
              onChange={onChange}
            />
          </WiniBox>

          {batchData.enableAssignType &&
            requiresMember(batchData.assignType) && (
              <WiniSelect
                label="배정 사용자"
                name="currentMemberId"
                value={batchData.currentMemberId}
                labelProps={{ shrink: true }}
                displayEmpty
                className="w-full"
                onChange={onChange}
                disabled={isSubmitting || isOptionsUnavailable}
              >
                {(userList || []).map((u) => (
                  <WiniMenuItem value={u.id} key={u.id}>
                    {u.fullName || u.username}
                  </WiniMenuItem>
                ))}
              </WiniSelect>
            )}
        </WiniBox>
      </WiniDialogContent>

      <WiniDialogActions>
        <WiniButton
          onClick={onSubmit}
          loading={isSubmitting}
          disabled={isSubmitting || isOptionsUnavailable}
        >
          변경
        </WiniButton>
        <WiniButton ui="lineGray" onClick={onClose} disabled={isSubmitting}>
          취소
        </WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
