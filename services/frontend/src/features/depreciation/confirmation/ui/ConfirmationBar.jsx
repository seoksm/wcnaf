import { WiniBox, WiniButton, WiniText } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';

/**
 * 결산 확정/해제 실행 바 (S-232)
 */
export const ConfirmationBar = ({
  confirmed,
  releaseReason,
  onReleaseReasonChange,
  onConfirm,
  onRelease,
  onOpenLog,
  isSubmitting,
  isLogLoading,
  disabled,
}) => {
  const actionDisabled = disabled || isSubmitting || isLogLoading;

  return (
    <WiniBox className="flex items-center justify-between gap-2 mb-2 flex-wrap">
      <WiniBox ui="noAutoGap" className="flex items-center gap-2 flex-wrap">
        {!confirmed &&
          winiCom.checkMenuAut(
            'update',
            <WiniButton
              onClick={onConfirm}
              loading={isSubmitting}
              disabled={actionDisabled}
            >
              결산 확정
            </WiniButton>,
          )}

        {confirmed && (
          <>
            <WiniText
              ui="row"
              label="해제 사유"
              variant="outlined"
              slotProps={{ inputLabel: { shrink: true } }}
              className="w-64"
              value={releaseReason || ''}
              onChange={(e) => onReleaseReasonChange?.(e.target.value)}
              disabled={actionDisabled}
            />
            {winiCom.checkMenuAut(
              'update',
              <WiniButton
                ui="lineGray"
                onClick={onRelease}
                loading={isSubmitting}
                disabled={actionDisabled}
              >
                확정 해제
              </WiniButton>,
            )}
          </>
        )}
      </WiniBox>

      <WiniBox ui="noAutoGap">
        {winiCom.checkMenuAut(
          'select',
          <WiniButton
            ui="lineGray"
            onClick={onOpenLog}
            loading={isLogLoading}
            disabled={actionDisabled}
          >
            확정·해제 이력
          </WiniButton>,
        )}
      </WiniBox>
    </WiniBox>
  );
};
