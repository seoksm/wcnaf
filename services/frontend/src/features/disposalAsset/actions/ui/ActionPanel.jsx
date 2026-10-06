import {
  WiniBox,
  WiniButton,
  WiniNumber,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';
import { EnumSelect } from '@/shared/ui/enum-select';
import { winiCom, winiDate } from '@/shared/lib';
import {
  LIFE_STATUS_LABEL,
  DISPOSAL_REASON_CODE_LABEL,
} from '@/entities/tangibleAsset';

const formatDateTime = (value) =>
  value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD HH:mm') : '-';
const formatAmount = (value) =>
  value == null ? '-' : `${Number(value).toLocaleString()}원`;

/**
 * 불용자산 목록에서 선택한 자산의 상세 · 복귀(S-240) · 처분 처리(S-242) 패널.
 * lifeStatus가 DISUSE면 복귀 버튼과 처분 처리 폼을, DISPOSED면 확정된 처분 내역을 보여준다.
 */
export const ActionPanel = ({
  selectedRow,
  disposeData,
  onDisposeChange,
  isDisposing,
  onDispose,
  isRestoring,
  onRestore,
  isActing,
}) => {
  if (!selectedRow) {
    return (
      <WiniBox ui="info" className="p-4">
        <WiniTypography variant="span" className="text-sm text-gray-400">
          목록에서 자산을 선택하면 상세와 처리 항목이 표시됩니다.
        </WiniTypography>
      </WiniBox>
    );
  }

  const isDisuse = selectedRow.lifeStatus === 'DISUSE';
  const isDisposed = selectedRow.lifeStatus === 'DISPOSED';

  return (
    <WiniBox ui="info" className="p-4">
      <WiniBox className="mb-3 flex items-center justify-between">
        <WiniTypography
          variant="span"
          className="text-sm font-bold text-text-main"
        >
          {selectedRow.assetName} ({selectedRow.assetCode})
        </WiniTypography>
        <WiniTypography variant="span" className="text-sm text-text-sub">
          {LIFE_STATUS_LABEL[selectedRow.lifeStatus] || selectedRow.lifeStatus}
        </WiniTypography>
      </WiniBox>

      <WiniBox className="mb-3 flex flex-col gap-1">
        <WiniTypography variant="span" className="text-xs text-text-sub">
          {isDisposed ? '처분 시점 장부가' : '현재 장부가'}{' '}
          {formatAmount(selectedRow.bookValue)}
        </WiniTypography>
        <WiniTypography variant="span" className="text-xs text-text-sub">
          {LIFE_STATUS_LABEL[selectedRow.lifeStatus] || selectedRow.lifeStatus}{' '}
          전환 시점 {formatDateTime(selectedRow.lifeStatusChangedAt)}
        </WiniTypography>
      </WiniBox>

      {isDisuse &&
        winiCom.checkMenuAut(
          'update',
          <WiniButton
            ui="lineGray"
            className="mb-3"
            onClick={() => onRestore(selectedRow.tangibleAssetId)}
            loading={isRestoring}
            disabled={isActing}
          >
            사용 상태로 복귀
          </WiniButton>,
        )}

      {isDisposed && (
        <WiniBox className="flex flex-col gap-1 rounded border border-solid border-gray-200 bg-white px-3 py-2">
          <WiniTypography variant="span" className="text-xs text-text-sub">
            처분 사유{' '}
            {DISPOSAL_REASON_CODE_LABEL[selectedRow.disposalReasonCode] || '-'}
          </WiniTypography>
          <WiniTypography variant="span" className="text-xs text-text-sub">
            처분 금액 {formatAmount(selectedRow.disposalAmount)}
          </WiniTypography>
          <WiniTypography variant="span" className="text-xs text-text-sub">
            거래처 {selectedRow.counterparty || '-'}
          </WiniTypography>
          <WiniTypography variant="span" className="text-xs text-text-sub">
            처분손익 {formatAmount(selectedRow.disposalGainLoss)}
          </WiniTypography>
          <WiniTypography variant="span" className="text-xs text-text-sub">
            처분일시 {formatDateTime(selectedRow.disposedAt)}
          </WiniTypography>
          {selectedRow.disposalMemo && (
            <WiniTypography variant="span" className="text-xs text-text-sub">
              메모 {selectedRow.disposalMemo}
            </WiniTypography>
          )}
        </WiniBox>
      )}

      {isDisuse && (
        <WiniBox className="mt-2 flex flex-col gap-2">
          <WiniTypography
            variant="span"
            className="text-sm font-bold text-text-main"
          >
            처분 처리
          </WiniTypography>

          <EnumSelect
            ui="column"
            label="처분 사유"
            name="disposalReasonCode"
            required
            disabled={isActing}
            value={disposeData.disposalReasonCode}
            enums={DISPOSAL_REASON_CODE_LABEL}
            slotProps={{ inputLabel: { shrink: true } }}
            onChange={onDisposeChange}
          />

          <WiniNumber
            ui="column"
            label="처분 금액"
            name="disposalAmount"
            disabled={isActing}
            value={disposeData.disposalAmount}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onDisposeChange}
            inputProps={{
              allowNegative: false,
              decimalScale: 0,
              inputMode: 'numeric',
            }}
          />

          <WiniText
            ui="column"
            label="거래처"
            name="counterparty"
            disabled={isActing}
            value={disposeData.counterparty || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onDisposeChange}
          />

          <WiniText
            ui="column"
            label="메모"
            name="memo"
            multiline
            minRows={2}
            disabled={isActing}
            value={disposeData.memo || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onDisposeChange}
          />

          <WiniBox className="flex justify-end">
            {winiCom.checkMenuAut(
              'update',
              <WiniButton
                onClick={() => onDispose(selectedRow.tangibleAssetId)}
                loading={isDisposing}
                disabled={isActing}
              >
                처분 처리
              </WiniButton>,
            )}
          </WiniBox>
        </WiniBox>
      )}
    </WiniBox>
  );
};
