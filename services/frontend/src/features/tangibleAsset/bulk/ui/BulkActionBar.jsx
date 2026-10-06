import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';

/**
 * 유형자산 목록 상단 - 선택 건수 + 복제/일괄 변경 액션 (S-214/S-215)
 */
export const BulkActionBar = ({
  checkedCount,
  onDuplicate,
  onBatchUpdate,
  onPrintQrLabels,
  onOpenExcelUpsert,
  onOpenActivityLog,
  isGeneratingQr,
  isListLoading,
  isBatchDisabled,
}) => {
  return (
    <WiniBox ui="btnbox" className="mb-1">
      <WiniTypography variant="span" className="text-text-default">
        {checkedCount > 0 ? `${checkedCount}건 선택됨` : ''}
      </WiniTypography>
      <WiniBox ui="btnitem" className="flex-wrap">
        {winiCom.checkMenuAut(
          'select',
          <WiniButton
            ui="lineGray"
            className="w-24"
            onClick={onOpenActivityLog}
          >
            전체 활동 로그
          </WiniButton>,
        )}
        {winiCom.checkMenuAut(
          'insert',
          <WiniButton
            ui="lineGray"
            className="w-24"
            onClick={onOpenExcelUpsert}
            disabled={isListLoading}
          >
            엑셀 업서트
          </WiniButton>,
        )}
        {winiCom.checkMenuAut(
          'select',
          <WiniButton
            ui="lineGray"
            className="w-24"
            onClick={onPrintQrLabels}
            loading={isGeneratingQr}
            disabled={checkedCount === 0 || isGeneratingQr || isListLoading}
          >
            QR 라벨 발행
          </WiniButton>,
        )}
        {winiCom.checkMenuAut(
          'insert',
          <WiniButton
            ui="lineGray"
            className="w-20"
            onClick={onDuplicate}
            disabled={checkedCount !== 1 || isListLoading}
          >
            복제
          </WiniButton>,
        )}
        {winiCom.checkMenuAut(
          'update',
          <WiniButton
            ui="lineGray"
            className="w-20"
            onClick={onBatchUpdate}
            disabled={checkedCount === 0 || isListLoading || isBatchDisabled}
          >
            일괄 변경
          </WiniButton>,
        )}
      </WiniBox>
    </WiniBox>
  );
};
