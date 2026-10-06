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
  WiniIconButton,
  WiniMenuItem,
  WiniNumber,
  WiniSelect,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';
import { winiCom, winiDate, CloseIcon } from '@/shared/lib';

const NUMBER_INPUT_PROPS = { allowNegative: false, decimalScale: 0, inputMode: 'numeric' };
const NUMBER_INPUT_SX = { '& input': { textAlign: 'left' } };

const formatDate = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD') : '-');
const formatAmount = (value) => (value == null ? '-' : Number(value).toLocaleString());

/**
 * S-532/533 라이선스 상세 - 기본정보(보유·배정·잔여) + 구매내역 + 배정 사용자 + 포함 SW를
 * 하나의 모달 안에 구획으로 쌓아 보여준다(Rental S-512와 동일한 단순화 패턴).
 */
export const LicenseDetailDialog = ({
  row, purchaseRecords, assignedUsers, includedSoftware, vendorList, userList,
  isLoading, isActing, newRecord, newAssign, newSoftwareName,
  userNameById,
  onNewRecordChange, onNewRecordDateChange, onAddPurchaseRecord,
  onNewAssignChange, onAssignUser, onReleaseUser,
  onNewSoftwareNameChange, onAddIncludedSoftware, onRemoveIncludedSoftware,
  onEdit, onRemoveLicense, onClose,
}) => {
  const activeAssignedUsers = (assignedUsers || []).filter((a) => a.active);
  const releasedAssignedUsers = (assignedUsers || []).filter((a) => !a.active);

  return (
    <WiniDialog open={!!row} onClose={onClose} fullWidth maxWidth="md">
      <WiniDialogTitle>라이선스 상세</WiniDialogTitle>
      <WiniDialogContent>
        {row && (
          <WiniBox className="flex flex-col gap-4">
            <WiniBox className="flex items-center justify-between">
              <WiniBox>
                <WiniTypography variant="span" className="text-sm font-semibold">{row.name}</WiniTypography>
                {row.memo && (
                  <WiniTypography variant="span" className="block text-xs text-text-sub">{row.memo}</WiniTypography>
                )}
              </WiniBox>
              <span className={`rounded border border-solid px-2 py-0.5 text-xs font-semibold ${row.overAssigned ? 'border-red-300 text-red-600' : 'border-gray-300 text-text-sub'}`}>
                보유 {row.purchasedQuantity} · 배정 {row.assignedQuantity} · 잔여 {row.remainingQuantity}{row.overAssigned ? ' (초과배정)' : ''}
              </span>
            </WiniBox>

            {isLoading ? (
              <WiniTypography variant="span" className="text-xs text-text-sub">불러오는 중...</WiniTypography>
            ) : (
              <>
                <WiniBox>
                  <WiniTypography variant="span" className="block text-sm font-semibold">구매내역</WiniTypography>
                  <WiniTypography variant="span" className="mb-2 block text-xs text-text-sub">
                    총액은 수량×단가로 자동 계산됩니다. 직접 입력해 값을 바꿀 수도 있습니다.
                  </WiniTypography>
                  {/* 뷰포트가 900px(md) 밑으로 내려가면 WiniDialog(fullWidth+maxWidth="md")도 함께
                      좁아진다 - 6개 필드를 고정 3/3/2/2/1/1로만 두면 좁은 화면에서 넘치며 "총액"·"추가"가
                      다이얼로그 밖으로 잘려 보인다(md 미만 뷰포트에서 실측 확인). sm/xs 단계를 넓게 잡아
                      2~4행으로 접히도록 해 넘침 없이 줄바꿈되게 한다. */}
                  <WiniGridLayout container columnSpacing={2} rowSpacing={1} className="mb-2 items-end">
                    <WiniGridItem size={{ xs: 12, sm: 6, md: 3 }}>
                      <WiniSelect ui="column" label="공급사" className="w-full" value={newRecord.vendorId} displayEmpty onChange={(e) => onNewRecordChange('vendorId', e.target.value)}>
                        <WiniMenuItem value="">(선택 안함)</WiniMenuItem>
                        {vendorList.map((v) => (
                          <WiniMenuItem value={v.vendorId} key={v.vendorId}>{v.name}</WiniMenuItem>
                        ))}
                      </WiniSelect>
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 12, sm: 6, md: 3 }}>
                      <WiniDatePicker ui="column" label="구매일" className="w-full" value={newRecord.purchaseDate || ''} onChange={onNewRecordDateChange('purchaseDate')} slotProps={{ inputLabel: { shrink: true } }} />
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 6, sm: 4, md: 2 }}>
                      <WiniNumber
                        ui="column"
                        label="수량"
                        name="quantity"
                        className="w-full"
                        value={newRecord.quantity}
                        onChange={(e) => onNewRecordChange('quantity', e.target.value)}
                        slotProps={{ inputLabel: { shrink: true } }}
                        inputProps={NUMBER_INPUT_PROPS}
                        inputSx={NUMBER_INPUT_SX}
                      />
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 6, sm: 4, md: 2 }}>
                      <WiniNumber
                        ui="column"
                        label="단가"
                        name="unitPrice"
                        className="w-full"
                        value={newRecord.unitPrice}
                        onChange={(e) => onNewRecordChange('unitPrice', e.target.value)}
                        slotProps={{ inputLabel: { shrink: true } }}
                        inputProps={NUMBER_INPUT_PROPS}
                        inputSx={NUMBER_INPUT_SX}
                      />
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 6, sm: 4, md: 1 }}>
                      <WiniNumber
                        ui="column"
                        label="총액"
                        name="totalAmount"
                        className="w-full"
                        value={newRecord.totalAmount}
                        onChange={(e) => onNewRecordChange('totalAmount', e.target.value)}
                        slotProps={{ inputLabel: { shrink: true } }}
                        inputProps={NUMBER_INPUT_PROPS}
                        inputSx={NUMBER_INPUT_SX}
                      />
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 6, sm: 4, md: 1 }}>
                      <WiniButton ui="lineGray" className="w-full" onClick={onAddPurchaseRecord} disabled={isActing}>추가</WiniButton>
                    </WiniGridItem>
                  </WiniGridLayout>
                  <WiniBox className="flex flex-col gap-2">
                    {(purchaseRecords || []).map((r) => (
                      <WiniBox key={r.licensePurchaseRecordId} className="flex flex-wrap items-center gap-3 rounded border border-solid border-gray-200 p-2 text-xs">
                        <span className="font-semibold">{formatDate(r.purchaseDate)}</span>
                        <span className="text-text-sub">{r.vendorName || '공급사 미지정'}</span>
                        <span className="text-text-sub">수량 {r.quantity}</span>
                        <span className="text-text-sub">단가 {formatAmount(r.unitPrice)}원</span>
                        <span className="text-text-sub">총액 {formatAmount(r.totalAmount)}원</span>
                      </WiniBox>
                    ))}
                    {(purchaseRecords || []).length === 0 && (
                      <WiniTypography variant="span" className="text-xs text-text-sub">등록된 구매내역이 없습니다.</WiniTypography>
                    )}
                  </WiniBox>
                </WiniBox>

                <WiniBox>
                  <WiniTypography variant="span" className="mb-2 block text-sm font-semibold">배정 사용자</WiniTypography>
                  <WiniGridLayout container columnSpacing={2} rowSpacing={1} className="mb-2 items-end">
                    <WiniGridItem size={{ xs: 12, sm: 6, md: 5 }}>
                      <WiniSelect ui="column" label="임직원" className="w-full" value={newAssign.memberId} displayEmpty onChange={(e) => onNewAssignChange('memberId', e.target.value)}>
                        <WiniMenuItem value="">(선택)</WiniMenuItem>
                        {userList.map((u) => (
                          <WiniMenuItem value={u.id} key={u.id}>{u.fullName || u.username}</WiniMenuItem>
                        ))}
                      </WiniSelect>
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 12, sm: 6, md: 5 }}>
                      <WiniSelect ui="column" label="구매내역" className="w-full" value={newAssign.licensePurchaseRecordId} displayEmpty onChange={(e) => onNewAssignChange('licensePurchaseRecordId', e.target.value)}>
                        <WiniMenuItem value="">(미연결)</WiniMenuItem>
                        {purchaseRecords.map((r) => (
                          <WiniMenuItem value={r.licensePurchaseRecordId} key={r.licensePurchaseRecordId}>{formatDate(r.purchaseDate)} ({r.quantity}개)</WiniMenuItem>
                        ))}
                      </WiniSelect>
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 12, sm: 12, md: 2 }}>
                      <WiniButton ui="lineGray" className="w-full" onClick={onAssignUser} disabled={isActing}>배정</WiniButton>
                    </WiniGridItem>
                  </WiniGridLayout>
                  <WiniBox className="flex flex-col gap-2">
                    {activeAssignedUsers.map((a) => (
                      <WiniBox key={a.licenseAssignedUserId} className="flex flex-wrap items-center gap-3 rounded border border-solid border-gray-200 p-2 text-xs">
                        <span className="font-semibold">{userNameById(a.memberId)}</span>
                        <span className="text-text-sub">배정일 {formatDate(a.assignedAt)}</span>
                        {a.unlinkedPurchase && <span className="text-orange-600">미연결 배정</span>}
                        {a.releaseRequestedYn && <span className="text-blue-600">회수 요청됨</span>}
                        {winiCom.checkMenuAut('update', (
                          <WiniButton ui="lineGray" onClick={() => onReleaseUser(a.licenseAssignedUserId)} disabled={isActing}>회수</WiniButton>
                        ))}
                      </WiniBox>
                    ))}
                    {activeAssignedUsers.length === 0 && (
                      <WiniTypography variant="span" className="text-xs text-text-sub">배정된 임직원이 없습니다.</WiniTypography>
                    )}
                    {releasedAssignedUsers.length > 0 && (
                      <WiniTypography variant="span" className="text-xs text-text-sub">
                        회수 이력 {releasedAssignedUsers.length}건
                      </WiniTypography>
                    )}
                  </WiniBox>
                </WiniBox>

                <WiniBox>
                  <WiniTypography variant="span" className="mb-2 block text-sm font-semibold">포함 SW</WiniTypography>
                  <WiniGridLayout container columnSpacing={2} rowSpacing={1} className="mb-2 items-end">
                    <WiniGridItem size={{ xs: 9, sm: 10 }}>
                      <WiniText ui="column" label="소프트웨어명" className="w-full" value={newSoftwareName} onChange={(e) => onNewSoftwareNameChange(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} placeholder="예: Microsoft Word" />
                    </WiniGridItem>
                    <WiniGridItem size={{ xs: 3, sm: 2 }}>
                      <WiniButton ui="lineGray" className="w-full" onClick={onAddIncludedSoftware} disabled={isActing}>추가</WiniButton>
                    </WiniGridItem>
                  </WiniGridLayout>
                  <WiniBox className="flex flex-wrap gap-2">
                    {(includedSoftware || []).map((s) => (
                      <span key={s.licenseIncludedSoftwareId} className="flex items-center gap-1 rounded border border-solid border-gray-200 py-1 pl-2 pr-1 text-xs">
                        {s.softwareName}
                        {winiCom.checkMenuAut('delete', (
                          <WiniIconButton
                            size="small"
                            aria-label={`${s.softwareName} 삭제`}
                            onClick={() => onRemoveIncludedSoftware(s.licenseIncludedSoftwareId)}
                            disabled={isActing}
                            sx={{ padding: '2px' }}
                          >
                            <CloseIcon sx={{ fontSize: 14 }} />
                          </WiniIconButton>
                        ))}
                      </span>
                    ))}
                    {(includedSoftware || []).length === 0 && (
                      <WiniTypography variant="span" className="text-xs text-text-sub">포함된 소프트웨어가 없습니다.</WiniTypography>
                    )}
                  </WiniBox>
                </WiniBox>
              </>
            )}
          </WiniBox>
        )}
      </WiniDialogContent>
      <WiniDialogActions>
        {winiCom.checkMenuAut('delete', (
          <WiniButton ui="delete" onClick={onRemoveLicense} disabled={isActing}>삭제</WiniButton>
        ))}
        {winiCom.checkMenuAut('update', (
          <WiniButton ui="lineGray" onClick={() => onEdit(row)}>수정</WiniButton>
        ))}
        <WiniButton ui="lineGray" onClick={onClose}>닫기</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
