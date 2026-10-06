import { WiniBox, WiniButton, WiniMenuItem, WiniSelect, WiniText, WiniTypography } from '@/shared/ui/wini';
import { EnumSelect } from '@/shared/ui/enum-select';
import { winiCom } from '@/shared/lib';
import { ANOMALY_TYPE_LABEL, CLOSURE_ACTION_LABEL, CLOSURE_REASON_CODE_LABEL } from '@/entities/inventory';

/**
 * 선택한 검수 결과 행에 대한 처리 - 상태에 따라 다른 액션을 보여준다.
 * - UNCONFIRMED: 관리자 대체 확인/이상보고(S-310/311 대체) · 종결 처리(S-308)
 * - PENDING_APPROVAL: 승인/반려(S-304)
 * - CONFIRMED/ANOMALY: 읽기 전용
 * 종결 처리 폼은 선택한 행 1건뿐 아니라, 목록에서 체크한 여러 건 일괄 처리(분실 제외, R6)에도 쓴다.
 */
export const ResultActionsPanel = ({
  selectedRow,
  checkedCount,
  anomalyForm,
  onAnomalyFormChange,
  closeForm,
  onCloseFormChange,
  isActing,
  onAdminConfirm,
  onAdminAnomaly,
  onApprove,
  onReject,
  onCloseOne,
  onCloseBulk,
  onFinalize,
}) => {
  const isUnconfirmed = selectedRow?.status === 'UNCONFIRMED';
  const isPendingApproval = selectedRow?.status === 'PENDING_APPROVAL';

  return (
    <WiniBox ui="info" className="flex flex-col gap-4 p-4">
      <WiniBox className="flex items-center justify-between">
        <WiniTypography variant="span" className="text-sm font-bold text-text-main">
          {selectedRow ? `${selectedRow.assetName} (${selectedRow.assetCode})` : '목록에서 자산을 선택하세요'}
        </WiniTypography>
        {winiCom.checkMenuAut(
          'update',
          <WiniButton ui="line" onClick={onFinalize} disabled={isActing}>조사 종료 확정</WiniButton>,
        )}
      </WiniBox>

      {isUnconfirmed && (
        <WiniBox className="flex flex-col gap-2 rounded border border-solid border-gray-200 bg-white p-3">
          <WiniTypography variant="span" className="text-sm font-semibold">확인 처리 (관리자 대체)</WiniTypography>
          {winiCom.checkMenuAut(
            'update',
            <WiniButton ui="lineGray" onClick={() => onAdminConfirm(selectedRow.inventoryTargetId)} loading={isActing} disabled={isActing}>
              확인 처리
            </WiniButton>,
          )}

          <EnumSelect
            ui="column"
            label="이상 유형"
            name="anomalyType"
            value={anomalyForm.anomalyType}
            enums={ANOMALY_TYPE_LABEL}
            slotProps={{ inputLabel: { shrink: true } }}
            onChange={onAnomalyFormChange}
          />
          <WiniText
            ui="column"
            label="메모"
            name="note"
            value={anomalyForm.note || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onAnomalyFormChange}
          />
          {winiCom.checkMenuAut(
            'update',
            <WiniButton ui="lineGray" onClick={() => onAdminAnomaly(selectedRow.inventoryTargetId)} loading={isActing} disabled={isActing}>
              이상 보고
            </WiniButton>,
          )}
        </WiniBox>
      )}

      {isPendingApproval && (
        <WiniBox className="flex gap-2">
          {winiCom.checkMenuAut(
            'update',
            <WiniButton onClick={() => onApprove(selectedRow.inventoryTargetId)} loading={isActing} disabled={isActing}>승인</WiniButton>,
          )}
          {winiCom.checkMenuAut(
            'update',
            <WiniButton ui="lineGray" onClick={() => onReject(selectedRow.inventoryTargetId)} loading={isActing} disabled={isActing}>
              반려
            </WiniButton>,
          )}
        </WiniBox>
      )}

      <WiniBox className="flex flex-col gap-2 rounded border border-solid border-gray-200 bg-white p-3">
        <WiniTypography variant="span" className="text-sm font-semibold">
          미확인 자산 종결 처리 (선택 1건 또는 목록에서 체크한 {checkedCount}건 일괄)
        </WiniTypography>

        <WiniSelect
          ui="column"
          label="처리 방법"
          name="closureAction"
          required
          value={closeForm.closureAction}
          displayEmpty
          slotProps={{ inputLabel: { shrink: true } }}
          className="w-full"
          onChange={onCloseFormChange}
        >
          <WiniMenuItem value="">선택</WiniMenuItem>
          {Object.entries(CLOSURE_ACTION_LABEL).map(([value, label]) => (
            <WiniMenuItem value={value} key={value}>{label}</WiniMenuItem>
          ))}
        </WiniSelect>

        <EnumSelect
          ui="column"
          label="사유"
          name="closureReasonCode"
          required
          value={closeForm.closureReasonCode}
          enums={CLOSURE_REASON_CODE_LABEL}
          slotProps={{ inputLabel: { shrink: true } }}
          onChange={onCloseFormChange}
        />
        <WiniText
          ui="column"
          label="메모"
          name="note"
          value={closeForm.note || ''}
          slotProps={{ inputLabel: { shrink: true } }}
          className="w-full"
          onChange={onCloseFormChange}
        />

        <WiniBox className="flex gap-2">
          {isUnconfirmed &&
            winiCom.checkMenuAut(
              'update',
              <WiniButton ui="lineGray" onClick={() => onCloseOne(selectedRow.inventoryTargetId)} loading={isActing} disabled={isActing}>
                선택 1건 종결
              </WiniButton>,
            )}
          {winiCom.checkMenuAut(
            'update',
            <WiniButton ui="lineGray" onClick={onCloseBulk} loading={isActing} disabled={isActing || checkedCount === 0}>
              체크한 {checkedCount}건 일괄 종결
            </WiniButton>,
          )}
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
