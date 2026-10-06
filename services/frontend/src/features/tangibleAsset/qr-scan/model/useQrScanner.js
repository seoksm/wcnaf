import { useCallback, useEffect, useRef, useState } from 'react';

const UUID_PATTERN =
  /[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/;

const extractAssetIdFromScan = (text) => {
  if (!text) return null;

  try {
    const url = new URL(text);
    const match = url.pathname.match(/\/asset-scan\/([0-9a-fA-F-]{36})/);
    if (match) return match[1];
  } catch {
    // URL 형식이 아닌 QR은 아래 UUID 패턴으로 판별한다.
  }

  const match = text.match(UUID_PATTERN);
  return match ? match[0] : null;
};

const getCameraErrorMessage = (error) => {
  switch (error?.name) {
    case 'NotAllowedError':
    case 'PermissionDeniedError':
      return '카메라 권한이 거부되었습니다. 브라우저와 운영체제 설정에서 카메라 접근을 허용해주세요.';
    case 'NotFoundError':
    case 'DevicesNotFoundError':
      return '사용 가능한 카메라 장치를 찾을 수 없습니다.';
    case 'NotReadableError':
    case 'TrackStartError':
      return '카메라를 열 수 없습니다. 다른 프로그램이 카메라를 사용 중인지 확인해주세요.';
    case 'OverconstrainedError':
    case 'ConstraintNotSatisfiedError':
      return '요청한 카메라 조건에 맞는 장치를 찾을 수 없습니다.';
    case 'SecurityError':
      return 'HTTPS 보안 연결이 아니어서 카메라를 사용할 수 없습니다.';
    default:
      return `카메라를 사용할 수 없습니다. (${error?.name || '알 수 없는 오류'})`;
  }
};

export const useQrScanner = (onDecoded) => {
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
  const streamRef = useRef(null);
  const animationFrameRef = useRef(null);
  const detectorRef = useRef(null);
  const onDecodedRef = useRef(onDecoded);
  const sessionRef = useRef(0);
  const activeRef = useRef(false);
  const scannedRef = useRef(false);
  const mountedRef = useRef(false);

  const [error, setError] = useState(null);
  const [isScanning, setIsScanning] = useState(false);

  onDecodedRef.current = onDecoded;

  const stopMedia = useCallback(() => {
    activeRef.current = false;

    if (animationFrameRef.current !== null) {
      cancelAnimationFrame(animationFrameRef.current);
      animationFrameRef.current = null;
    }

    streamRef.current?.getTracks().forEach((track) => track.stop());
    streamRef.current = null;
    detectorRef.current = null;

    if (videoRef.current) {
      videoRef.current.srcObject = null;
    }
  }, []);

  const handleDecoded = useCallback(
    (text) => {
      if (scannedRef.current) return;

      const tangibleAssetId = extractAssetIdFromScan(text);
      if (!tangibleAssetId) return;

      scannedRef.current = true;
      sessionRef.current += 1;
      stopMedia();
      if (mountedRef.current) setIsScanning(false);
      onDecodedRef.current?.(tangibleAssetId);
    },
    [stopMedia],
  );

  const scanWithBarcodeDetector = useCallback(async () => {
    if (!activeRef.current || !videoRef.current || !detectorRef.current) return;

    try {
      const barcodes = await detectorRef.current.detect(videoRef.current);
      if (barcodes.length > 0) {
        handleDecoded(barcodes[0].rawValue);
      }
    } catch {
      // 일시적인 프레임 디코딩 실패는 다음 프레임에서 재시도한다.
    }

    if (activeRef.current) {
      animationFrameRef.current = requestAnimationFrame(
        scanWithBarcodeDetector,
      );
    }
  }, [handleDecoded]);

  const scanWithJsQr = useCallback(
    (decode) => {
      if (!activeRef.current) return;

      const video = videoRef.current;
      const canvas = canvasRef.current;

      if (video && canvas && video.readyState === video.HAVE_ENOUGH_DATA) {
        try {
          canvas.width = video.videoWidth;
          canvas.height = video.videoHeight;

          const context = canvas.getContext('2d', { willReadFrequently: true });
          if (context) {
            context.drawImage(video, 0, 0, canvas.width, canvas.height);
            const imageData = context.getImageData(
              0,
              0,
              canvas.width,
              canvas.height,
            );
            const code = decode(
              imageData.data,
              imageData.width,
              imageData.height,
            );
            if (code?.data) handleDecoded(code.data);
          }
        } catch {
          // 영상 크기 변경 중 발생할 수 있는 프레임 오류는 다음 프레임에서 재시도한다.
        }
      }

      if (activeRef.current) {
        animationFrameRef.current = requestAnimationFrame(() =>
          scanWithJsQr(decode),
        );
      }
    },
    [handleDecoded],
  );

  const startScanner = useCallback(async () => {
    const sessionId = sessionRef.current + 1;
    sessionRef.current = sessionId;
    stopMedia();
    scannedRef.current = false;

    if (mountedRef.current) {
      setError(null);
      setIsScanning(false);
    }

    if (!window.isSecureContext) {
      if (mountedRef.current) {
        setError(
          'HTTPS 보안 연결이 아니어서 카메라를 사용할 수 없습니다. localhost 또는 https://로 접속해주세요.',
        );
      }
      return;
    }

    if (!navigator.mediaDevices?.getUserMedia) {
      if (mountedRef.current) {
        setError(
          '이 브라우저에서는 카메라 기능을 사용할 수 없습니다. 지원되는 브라우저에서 다시 시도해주세요.',
        );
      }
      return;
    }

    let stream;
    try {
      stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: { ideal: 'environment' } },
      });
    } catch (preferredError) {
      if (sessionRef.current !== sessionId || !mountedRef.current) return;

      const canRetryWithoutFacingMode = [
        'OverconstrainedError',
        'ConstraintNotSatisfiedError',
        'NotFoundError',
        'DevicesNotFoundError',
      ].includes(preferredError?.name);

      if (!canRetryWithoutFacingMode) {
        if (sessionRef.current === sessionId && mountedRef.current) {
          setError(getCameraErrorMessage(preferredError));
        }
        return;
      }

      try {
        stream = await navigator.mediaDevices.getUserMedia({ video: true });
      } catch (fallbackError) {
        if (sessionRef.current === sessionId && mountedRef.current) {
          setError(getCameraErrorMessage(fallbackError));
        }
        return;
      }
    }

    if (sessionRef.current !== sessionId || !mountedRef.current) {
      stream.getTracks().forEach((track) => track.stop());
      return;
    }

    try {
      streamRef.current = stream;
      videoRef.current.srcObject = stream;
      await videoRef.current.play();

      if (sessionRef.current !== sessionId || !mountedRef.current) {
        stream.getTracks().forEach((track) => track.stop());
        return;
      }

      activeRef.current = true;
      setIsScanning(true);

      if ('BarcodeDetector' in window) {
        try {
          detectorRef.current = new window.BarcodeDetector({
            formats: ['qr_code'],
          });
          animationFrameRef.current = requestAnimationFrame(
            scanWithBarcodeDetector,
          );
          return;
        } catch {
          detectorRef.current = null;
        }
      }

      const { default: decode } = await import('jsqr');
      if (sessionRef.current === sessionId && activeRef.current) {
        scanWithJsQr(decode);
      }
    } catch (cameraError) {
      stream.getTracks().forEach((track) => track.stop());
      if (sessionRef.current !== sessionId) return;

      stopMedia();
      if (!mountedRef.current) return;

      setIsScanning(false);
      setError(getCameraErrorMessage(cameraError));
    }
  }, [scanWithBarcodeDetector, scanWithJsQr, stopMedia]);

  useEffect(() => {
    mountedRef.current = true;
    startScanner();

    return () => {
      mountedRef.current = false;
      sessionRef.current += 1;
      stopMedia();
    };
  }, [startScanner, stopMedia]);

  return {
    videoRef,
    canvasRef,
    isScanning,
    error,
    retry: startScanner,
  };
};
