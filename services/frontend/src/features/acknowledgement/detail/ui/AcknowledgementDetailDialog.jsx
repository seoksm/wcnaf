import {
  WiniBox,
  WiniButton,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniGridItem,
  WiniGridLayout,
  WiniMenuItem,
  WiniSelect,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { ACK_STATUS_LABEL, RETURN_CONDITION_LABEL } from '@/entities/acknowledgement';
import { ACK_TYPE_LABEL } from '@/entities/ackTemplate';
import { LIFE_STATUS_LABEL, ASSIGN_TYPE_LABEL } from '@/entities/tangibleAsset';

const formatDateTime = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD HH:mm') : '-');

/** S-432 확인서 상세 · 담당자 승인 - 좌: 문서 본문 스냅샷, 우: 승인 증빙 + 담당자 액션 */
export const AcknowledgementDetailDialog = ({
  open, detail, isLoading, isActing,
  approveForm, onApproveFormChange, onSubmitApprove,
  cancelReason, onCancelReasonChange, onSubmitCancel,
  onClose,
}) => {
  const cancellable = detail && (detail.status === 'PENDING_EMPLOYEE' || detail.status === 'PENDING_MANAGER');

  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <WiniDialogTitle>확인서 상세</WiniDialogTitle>
      <WiniDialogContent>
        {isLoading || !detail ? (
          <WiniTypography variant="span" className="block py-4 text-center text-text-sub">불러오는 중...</WiniTypography>
        ) : (
          <WiniGridLayout container columnSpacing={2} rowSpacing={2}>
            <WiniGridItem size={{ md: 6, xs: 12 }}>
              <WiniBox className="mb-2 flex items-center justify-between">
                <WiniTypography variant="span" className="text-sm font-semibold">
                  {ACK_TYPE_LABEL[detail.type]}확인서 - {detail.assetName}({detail.assetCode})
                </WiniTypography>
                <span className="rounded border border-solid border-gray-300 px-2 py-0.5 text-xs">
                  {ACK_STATUS_LABEL[detail.status] || detail.status}
                </span>
              </WiniBox>
              <WiniBox ui="info" className="whitespace-pre-wrap p-3 text-sm">{detail.bodySnapshot}</WiniBox>
              {detail.cancelledYn && (
                <WiniTypography variant="span" className="mt-2 block text-xs text-red-500">
                  취소됨 - {detail.cancelledReason}
                </WiniTypography>
              )}
            </WiniGridItem>

            <WiniGridItem size={{ md: 6, xs: 12 }}>
              <WiniTypography variant="span" className="mb-2 block text-sm font-semibold">승인 증빙</WiniTypography>
              <WiniBox className="mb-3 flex flex-col gap-2">
                {(detail.approvals || []).map((a) => (
                  <WiniBox key={a.acknowledgementApprovalId} className="rounded border border-solid border-gray-200 p-2 text-xs">
                    <WiniTypography variant="span" className="block font-semibold">
                      {a.approvalStep === 'EMPLOYEE' ? '임직원 승인' : '담당자 승인'}
                    </WiniTypography>
                    <WiniTypography variant="span" className="block text-text-sub">
                      {formatDateTime(a.approvedAt)} · IP {a.approverIpMasked || '-'}
                    </WiniTypography>
                    <WiniTypography variant="span" className="block text-text-sub">{a.assetSnapshot}</WiniTypography>
                  </WiniBox>
                ))}
                {(detail.approvals || []).length === 0 && (
                  <WiniTypography variant="span" className="text-xs text-text-sub">아직 승인 이력이 없습니다.</WiniTypography>
                )}
              </WiniBox>

              {detail.status === 'PENDING_MANAGER' && (
                <WiniBox className="flex flex-col gap-2 rounded border border-solid border-gray-200 p-3">
                  <WiniTypography variant="span" className="text-sm font-semibold">담당자 승인 - 반납 후 상태 지정</WiniTypography>
                  <WiniBox className="flex gap-2">
                    {Object.entries(RETURN_CONDITION_LABEL).map(([value, label]) => (
                      <WiniButton
                        key={value}
                        ui={approveForm.returnCondition === value ? 'default' : 'lineGray'}
                        onClick={() => onApproveFormChange({ target: { name: 'returnCondition', value } })}
                      >
                        {label}
                      </WiniButton>
                    ))}
                  </WiniBox>
                  <WiniSelect ui="column" label="생애상태" name="nextLifeStatus" className="w-full" value={approveForm.nextLifeStatus} onChange={onApproveFormChange}>
                    {Object.entries(LIFE_STATUS_LABEL).map(([value, label]) => (
                      <WiniMenuItem value={value} key={value}>{label}</WiniMenuItem>
                    ))}
                  </WiniSelect>
                  <WiniSelect ui="column" label="배정형태" name="nextAssignType" className="w-full" value={approveForm.nextAssignType} onChange={onApproveFormChange}>
                    {['UNASSIGNED', 'SHARED', 'LOANABLE'].map((value) => (
                      <WiniMenuItem value={value} key={value}>{ASSIGN_TYPE_LABEL[value]}</WiniMenuItem>
                    ))}
                  </WiniSelect>
                  <WiniButton ui="line" onClick={onSubmitApprove} loading={isActing} disabled={isActing}>담당자 승인</WiniButton>
                </WiniBox>
              )}

              {cancellable && (
                <WiniBox className="mt-3 flex flex-col gap-2 rounded border border-solid border-gray-200 p-3">
                  <WiniTypography variant="span" className="text-sm font-semibold">요청 취소</WiniTypography>
                  <WiniText ui="column" label="취소 사유" className="w-full" value={cancelReason} onChange={(e) => onCancelReasonChange(e.target.value)} />
                  <WiniButton ui="delete" onClick={onSubmitCancel} loading={isActing} disabled={isActing}>요청 취소</WiniButton>
                </WiniBox>
              )}
            </WiniGridItem>
          </WiniGridLayout>
        )}
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="lineGray" onClick={onClose}>닫기</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
