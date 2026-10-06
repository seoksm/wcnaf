import {
  WiniButton,
  WiniDatePicker,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniGridItem,
  WiniGridLayout,
  WiniMenuItem,
  WiniSelect,
  WiniText,
} from '@/shared/ui/wini';
import { INTANGIBLE_TYPE_LABEL } from '@/entities/intangibleAsset';

/** S-501 무형자산 등록·수정 - P-6 모달 */
export const IntangibleAssetDialog = ({ open, form, userList, isSubmitting, onClose, onChange, onDateChange, onSubmit }) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <WiniDialogTitle>{form.intangibleAssetId ? '무형자산 수정' : '무형자산 등록'}</WiniDialogTitle>
      <WiniDialogContent>
        <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1} className="pt-2">
          <WiniGridItem xs={12}>
            <WiniSelect ui="column" label="구분" name="intangibleType" className="w-full" value={form.intangibleType} onChange={onChange}>
              {Object.entries(INTANGIBLE_TYPE_LABEL).map(([value, label]) => (
                <WiniMenuItem value={value} key={value}>{label}</WiniMenuItem>
              ))}
            </WiniSelect>
          </WiniGridItem>
          <WiniGridItem xs={12}>
            <WiniText ui="column" label="자산명" name="name" required className="w-full" value={form.name} onChange={onChange} slotProps={{ inputLabel: { shrink: true } }} />
          </WiniGridItem>
          <WiniGridItem xs={12}>
            <WiniText ui="column" label="발급기관" name="issuer" className="w-full" value={form.issuer} onChange={onChange} slotProps={{ inputLabel: { shrink: true } }} />
          </WiniGridItem>
          <WiniGridItem xs={12}>
            <WiniDatePicker ui="column" label="등록일" name="registeredDate" className="w-full" value={form.registeredDate || ''} onChange={onDateChange('registeredDate')} slotProps={{ inputLabel: { shrink: true } }} />
          </WiniGridItem>
          <WiniGridItem xs={12}>
            <WiniDatePicker ui="column" label="만료일" required name="expiryDate" className="w-full" value={form.expiryDate || ''} onChange={onDateChange('expiryDate')} slotProps={{ inputLabel: { shrink: true } }} />
          </WiniGridItem>
          <WiniGridItem xs={12}>
            <WiniSelect ui="column" label="담당자" name="ownerMemberId" className="w-full" value={form.ownerMemberId} displayEmpty onChange={onChange}>
              <WiniMenuItem value="">(미지정)</WiniMenuItem>
              {userList.map((user) => (
                <WiniMenuItem value={user.id} key={user.id}>{user.fullName || user.username}</WiniMenuItem>
              ))}
            </WiniSelect>
          </WiniGridItem>
          <WiniGridItem xs={12}>
            <WiniText
              ui="column"
              label="알림 시점(콤마 구분, 예: 30,7,3,1)"
              name="alertDays"
              className="w-full"
              value={form.alertDays}
              onChange={onChange}
              slotProps={{ inputLabel: { shrink: true } }}
            />
          </WiniGridItem>
          <WiniGridItem xs={12}>
            <WiniText ui="column" label="메모" name="memo" multiline minRows={2} className="w-full" value={form.memo} onChange={onChange} />
          </WiniGridItem>
        </WiniGridLayout>
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="line" onClick={onSubmit} loading={isSubmitting} disabled={isSubmitting}>저장</WiniButton>
        <WiniButton ui="lineGray" onClick={onClose}>취소</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
