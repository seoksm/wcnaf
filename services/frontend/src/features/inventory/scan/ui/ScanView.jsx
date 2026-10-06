import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { useContinuousQrScanner } from '../model/useContinuousQrScanner';

/**
 * S-311 연속 스캔 검수 - 스캐너를 닫지 않고 연달아 읽는다(§3 핵심 차별점). 매 성공 스캔마다
 * onDecoded가 호출되고 카메라는 계속 켜져 있다. lastFeedback으로 방금 스캔 결과를 화면에 보여준다.
 */
export const ScanView = ({ onDecoded, lastFeedback, remainingCount, onFinish, onBack }) => {
  const { videoRef, isScanning, error } = useContinuousQrScanner(onDecoded);

  return (
    <WiniBox className="mx-auto flex max-w-[480px] flex-col items-center gap-3 p-4">
      <WiniBox className="flex w-full items-center justify-between">
        <WiniButton ui="lineGray" onClick={onBack}>목록으로</WiniButton>
        <WiniTypography variant="span" className="text-sm text-text-sub">
          남은 {remainingCount}건
        </WiniTypography>
      </WiniBox>

      <WiniTypography variant="h2">연속 스캔 검수</WiniTypography>
      <WiniTypography variant="span" className="text-center text-text-sub">
        자산에 부착된 QR 라벨을 카메라에 순서대로 비춰주세요. 스캐너는 닫히지 않습니다.
      </WiniTypography>

      {error ? (
        <WiniBox className="p-4 text-center text-red-500">{error}</WiniBox>
      ) : (
        <WiniBox className="aspect-square w-full overflow-hidden rounded bg-black">
          <video ref={videoRef} muted playsInline className="h-full w-full object-cover" />
        </WiniBox>
      )}

      {!error && !isScanning && (
        <WiniTypography variant="span" className="text-text-sub">카메라를 여는 중...</WiniTypography>
      )}

      {lastFeedback && (
        <WiniBox
          className={`w-full rounded p-3 text-center text-sm font-semibold ${
            lastFeedback.ok ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-600'
          }`}
        >
          {lastFeedback.message}
        </WiniBox>
      )}

      <WiniButton onClick={onFinish} disabled={remainingCount > 0} className="w-full">
        {remainingCount > 0 ? `완료 (남은 ${remainingCount}건)` : '완료'}
      </WiniButton>
    </WiniBox>
  );
};
