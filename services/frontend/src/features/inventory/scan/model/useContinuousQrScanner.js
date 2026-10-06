import { useCallback, useEffect, useRef, useState } from 'react';

const UUID_PATTERN = /[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/;

/** QR에 인코딩된 문자열(자산 상세 URL 또는 순수 UUID)에서 유형자산 ID를 추출한다 - useQrScanner(S-217)와 동일한 포맷 */
const extractAssetIdFromScan = (text) => {
  if (!text) return null;
  try {
    const url = new URL(text);
    const match = url.pathname.match(/\/asset-scan\/([0-9a-fA-F-]{36})/);
    if (match) return match[1];
  } catch {
    // URL 형식이 아니면 아래에서 UUID 패턴으로 재시도
  }
  const match = text.match(UUID_PATTERN);
  return match ? match[0] : null;
};

/** 같은 QR을 카메라 앞에 계속 대고 있어도 중복 트리거되지 않게 하는 최소 재인식 간격(ms) */
const RESCAN_COOLDOWN_MS = 2500;

/**
 * S-311 연속 스캔 검수 - useQrScanner(S-217)와 동일한 카메라/디코딩 로직(BarcodeDetector 우선,
 * jsQR 폴백)을 재사용하되, 단발성 navigate 대신 매 성공 스캔마다 onDecoded 콜백을 호출하고
 * 스캐너를 닫지 않는다("연속 스캔" - §3 핵심 차별점). 직전과 같은 자산을 재스캔했을 때만 짧은
 * 쿨다운을 둬서 중복 처리(같은 자산이 계속 화면에 잡혀서 매 프레임 콜백되는 것)를 막는다.
 */
export const useContinuousQrScanner = (onDecoded) => {
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
  const streamRef = useRef(null);
  const rafRef = useRef(null);
  const detectorRef = useRef(null);
  const lastDecodedRef = useRef({ assetId: null, at: 0 });
  const pausedRef = useRef(false);
  const onDecodedRef = useRef(onDecoded);
  onDecodedRef.current = onDecoded;

  const [error, setError] = useState(null);
  const [isScanning, setIsScanning] = useState(false);

  const pause = useCallback(() => {
    pausedRef.current = true;
  }, []);

  const resume = useCallback(() => {
    pausedRef.current = false;
  }, []);

  const handleDecoded = useCallback((text) => {
    if (pausedRef.current) return;
    const assetId = extractAssetIdFromScan(text);
    if (!assetId) return;

    const now = Date.now();
    const last = lastDecodedRef.current;
    if (last.assetId === assetId && now - last.at < RESCAN_COOLDOWN_MS) return;

    lastDecodedRef.current = { assetId, at: now };
    onDecodedRef.current?.(assetId);
  }, []);

  const scanLoopBarcodeDetector = useCallback(async () => {
    if (!videoRef.current || !detectorRef.current) return;
    try {
      const barcodes = await detectorRef.current.detect(videoRef.current);
      if (barcodes.length > 0) handleDecoded(barcodes[0].rawValue);
    } catch {
      // 프레임 디코딩 실패는 무시하고 다음 프레임에서 재시도
    }
    rafRef.current = requestAnimationFrame(scanLoopBarcodeDetector);
  }, [handleDecoded]);

  const scanLoopJsQR = useCallback((jsQR) => {
    const loop = () => {
      const video = videoRef.current;
      if (!video || video.readyState !== video.HAVE_ENOUGH_DATA) {
        rafRef.current = requestAnimationFrame(loop);
        return;
      }
      if (!canvasRef.current) canvasRef.current = document.createElement('canvas');
      const canvas = canvasRef.current;
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      const ctx = canvas.getContext('2d');
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
      const code = jsQR(imageData.data, imageData.width, imageData.height);
      if (code?.data) handleDecoded(code.data);
      rafRef.current = requestAnimationFrame(loop);
    };
    loop();
  }, [handleDecoded]);

  useEffect(() => {
    let cancelled = false;

    const describeError = (err) => {
      const name = err?.name;
      console.error('[연속 QR 스캐너] 카메라 접근 실패:', name, err?.message);
      switch (name) {
        case 'NotAllowedError':
        case 'PermissionDeniedError':
          return '카메라 권한이 거부되었습니다. 브라우저 설정에서 카메라 접근을 허용해주세요.';
        case 'NotFoundError':
        case 'DevicesNotFoundError':
          return '사용 가능한 카메라 장치를 찾을 수 없습니다.';
        case 'NotReadableError':
        case 'TrackStartError':
          return '카메라를 열 수 없습니다. 다른 앱이 카메라를 사용 중일 수 있습니다.';
        case 'SecurityError':
          return 'HTTPS(보안 연결)가 아니어서 카메라를 사용할 수 없습니다.';
        default:
          return `카메라를 사용할 수 없습니다. (${name || '알 수 없는 오류'})`;
      }
    };

    const start = async () => {
      if (!window.isSecureContext) {
        setError('HTTPS(보안 연결)가 아니어서 카메라를 사용할 수 없습니다.');
        return;
      }
      if (!navigator.mediaDevices?.getUserMedia) {
        setError('이 브라우저에서는 카메라 API를 사용할 수 없습니다.');
        return;
      }

      let stream;
      try {
        stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: { ideal: 'environment' } } });
      } catch (preferredErr) {
        try {
          stream = await navigator.mediaDevices.getUserMedia({ video: true });
        } catch (fallbackErr) {
          if (!cancelled) setError(describeError(fallbackErr || preferredErr));
          return;
        }
      }

      if (cancelled) {
        stream.getTracks().forEach((track) => track.stop());
        return;
      }

      try {
        streamRef.current = stream;
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
          await videoRef.current.play();
        }
        setIsScanning(true);

        if ('BarcodeDetector' in window) {
          try {
            detectorRef.current = new window.BarcodeDetector({ formats: ['qr_code'] });
            rafRef.current = requestAnimationFrame(scanLoopBarcodeDetector);
            return;
          } catch {
            // 생성 실패 시 jsQR 폴백으로 진행
          }
        }

        const { default: jsQR } = await import('jsqr');
        if (cancelled) return;
        scanLoopJsQR(jsQR);
      } catch (err) {
        setError(describeError(err));
      }
    };

    start();

    return () => {
      cancelled = true;
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
      if (streamRef.current) streamRef.current.getTracks().forEach((track) => track.stop());
    };
  }, [scanLoopBarcodeDetector, scanLoopJsQR]);

  return { videoRef, isScanning, error, pause, resume };
};
