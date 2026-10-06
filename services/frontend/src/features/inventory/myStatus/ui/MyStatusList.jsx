import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { PhotoCaptureButton } from '@/features/inventory/scan';
import { INVENTORY_RESULT_STATUS_LABEL } from '@/entities/inventory';

const STATUS_CHIP_CLASS = {
  UNCONFIRMED: 'border-gray-300 text-gray-600',
  PENDING_APPROVAL: 'border-blue-300 text-blue-600',
  CONFIRMED: 'border-green-300 text-green-600',
  ANOMALY: 'border-red-300 text-red-600',
};

const StatusChip = ({ status }) => (
  <span className={`rounded border border-solid px-2 py-0.5 text-xs font-semibold ${STATUS_CHIP_CLASS[status] || 'border-gray-300 text-gray-600'}`}>
    {INVENTORY_RESULT_STATUS_LABEL[status] || status}
  </span>
);

/**
 * S-310 내 전수조사 - 대상 목록. 미확인 행에만 "라벨 없음/훼손"(사진, I3 예외)과 "제 자산이
 * 아닙니다" 액션을 보여준다. 실제 확인은 원칙적으로 "QR 검수 시작"(S-311) 경로로 유도한다.
 */
export const MyStatusList = ({ status, isActing, onStartScan, onConfirmWithPhoto, onReportWrongHolder }) => {
  if (!status?.hasActiveInventory) {
    return (
      <WiniBox ui="info" className="p-6 text-center">
        <WiniTypography variant="span" className="text-text-sub">진행 중인 전수조사가 없습니다.</WiniTypography>
      </WiniBox>
    );
  }

  const results = status.results || [];
  const unconfirmedCount = results.filter((r) => r.status === 'UNCONFIRMED').length;

  return (
    <WiniBox className="flex flex-col gap-3">
      <WiniBox className="flex items-center justify-between">
        <WiniTypography variant="h2">{status.title}</WiniTypography>
        <WiniButton onClick={onStartScan} disabled={unconfirmedCount === 0}>QR 검수 시작</WiniButton>
      </WiniBox>

      {unconfirmedCount === 0 ? (
        <WiniBox ui="info" className="p-4 text-center">
          <WiniTypography variant="span" className="text-green-600 font-semibold">
            모든 대상 자산을 확인했습니다. 감사합니다.
          </WiniTypography>
        </WiniBox>
      ) : (
        <WiniTypography variant="span" className="text-sm text-text-sub">
          아직 확인하지 않은 자산이 {unconfirmedCount}건 있습니다. QR 라벨을 스캔해주세요.
        </WiniTypography>
      )}

      <WiniBox className="flex flex-col gap-2">
        {results.map((row) => (
          <WiniBox
            key={row.inventoryTargetId}
            className="flex flex-col gap-2 rounded border border-solid border-gray-200 bg-white p-3"
          >
            <WiniBox className="flex items-center justify-between">
              <WiniTypography variant="span" className="text-sm font-semibold">
                {row.assetName} ({row.assetCode})
              </WiniTypography>
              <StatusChip status={row.status} />
            </WiniBox>
            <WiniTypography variant="span" className="text-xs text-text-sub">{row.categoryName}</WiniTypography>

            {row.status === 'UNCONFIRMED' && (
              <WiniBox className="flex gap-2">
                <PhotoCaptureButton
                  label="라벨 없음/훼손 - 사진으로 확인"
                  isUploading={isActing}
                  onCapture={(file) => onConfirmWithPhoto(row.inventoryTargetId, file)}
                />
                <WiniButton ui="lineGray" onClick={() => onReportWrongHolder(row.inventoryTargetId)} disabled={isActing}>
                  제 자산이 아닙니다
                </WiniButton>
              </WiniBox>
            )}
          </WiniBox>
        ))}
      </WiniBox>
    </WiniBox>
  );
};
