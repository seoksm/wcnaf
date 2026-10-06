import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { useQrScanner } from '../model/useQrScanner';

export const QrScannerView = ({ onDecoded }) => {
  const { videoRef, canvasRef, isScanning, error, retry } =
    useQrScanner(onDecoded);

  return (
    <WiniBox className="mx-auto flex h-full w-full max-w-[640px] flex-col items-center gap-3 p-4">
      <WiniTypography variant="h2">QR 스캔</WiniTypography>
      <WiniTypography variant="span" className="text-center text-text-sub">
        자산에 부착된 QR 라벨을 카메라에 비춰주세요.
      </WiniTypography>

      <WiniBox
        ui="noAutoGap"
        className="aspect-[3/4] max-h-[65vh] w-full overflow-hidden rounded bg-black landscape:aspect-video"
      >
        {/* 브라우저 카메라 스트림과 프레임 디코딩을 연결하기 위한 native media 요소이다. */}
        <video
          ref={videoRef}
          autoPlay
          muted
          playsInline
          className="h-full w-full object-cover"
        />
        <canvas ref={canvasRef} className="hidden" aria-hidden="true" />
      </WiniBox>

      {error ? (
        <WiniBox
          ui="noAutoGap"
          className="flex w-full flex-col items-center gap-3 rounded bg-red-50 p-4 text-center text-red-600"
          role="alert"
        >
          <WiniTypography variant="span">{error}</WiniTypography>
          <WiniButton ui="line" onClick={retry}>
            다시 시도
          </WiniButton>
        </WiniBox>
      ) : (
        !isScanning && (
          <WiniTypography
            variant="span"
            className="text-text-sub"
            role="status"
          >
            카메라를 여는 중...
          </WiniTypography>
        )
      )}
    </WiniBox>
  );
};
