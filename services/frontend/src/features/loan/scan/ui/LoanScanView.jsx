import { useEffect } from 'react';
import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { useContinuousQrScanner } from '@/features/inventory/scan';

/**
 * S-420(대여)/S-421(반납) 공용 QR 스캔 화면 - 연속 스캔 검수(S-311, useContinuousQrScanner)와
 * 동일한 카메라/디코딩을 재사용한다. 대여/반납은 목표 건수가 정해져 있지 않아(원하는 만큼 스캔),
 * S-311의 "남은 N건" 강제 완료 버튼 대신 언제든 누를 수 있는 "완료"만 둔다.
 * `paused`가 true인 동안은 스캐너를 멈춰 API 처리 중이거나 반납 확인 다이얼로그가 떠 있는
 * 사이에 같은(또는 다른) QR이 다시 인식되는 것을 막는다 - 호출부가 각자의 "지금 처리 중" 상태를
 * 그대로 넘기면 된다.
 */
export const LoanScanView = ({ title, subtitle, onDecoded, lastFeedback, onBack, paused }) => {
  const { videoRef, isScanning, error, pause, resume } = useContinuousQrScanner(onDecoded);

  useEffect(() => {
    if (paused) {
      pause();
    } else {
      resume();
    }
  }, [paused, pause, resume]);

  return (
    <WiniBox className="mx-auto flex max-w-[480px] flex-col items-center gap-3 p-4">
      <WiniBox className="flex w-full items-center justify-between">
        <WiniButton ui="lineGray" onClick={onBack}>목록으로</WiniButton>
      </WiniBox>

      <WiniTypography variant="h2">{title}</WiniTypography>
      <WiniTypography variant="span" className="text-center text-text-sub">{subtitle}</WiniTypography>

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

      <WiniButton onClick={onBack} className="w-full">완료</WiniButton>
    </WiniBox>
  );
};
