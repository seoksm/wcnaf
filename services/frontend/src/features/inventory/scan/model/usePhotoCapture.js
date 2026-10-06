import { useCallback, useRef, useState } from 'react';
import {
  PHOTO_UPLOAD_ERROR_CODE,
  preparePhotoUpload,
  uploadPhotoToPresignedUrl,
} from '../api/api';
import { winiCom, winiDate } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

/**
 * C-201 모바일 촬영 (설계문서 §3) - <input capture="environment">로 앨범 선택을 막아 촬영을
 * 강제한다. 일부 브라우저가 capture 속성을 무시할 수 있어, 촬영 시각(파일을 받은 시각)과 업로드
 * 완료 시각을 둘 다 기록해 사후 판별 근거로 남긴다.
 *
 * presigned PUT은 인증·조직·메뉴 헤더를 사용하지 않는 전용 클라이언트로 전송한다. 개발 환경은
 * Vite의 /s3-proxy를 사용하고, 운영 HTTPS 환경은 VITE_S3_UPLOAD_PROXY_PREFIX 설정이 필요하다.
 */
export const usePhotoCapture = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const [isUploading, setIsUploading] = useState(false);
  const connectorRef = useRef(connector);
  const uploadingRef = useRef(false);

  connectorRef.current = connector;

  const capture = useCallback(async (file) => {
    const currentConnector = connectorRef.current;
    if (!file || !currentConnector || uploadingRef.current) return null;

    const capturedAt = winiDate.now().toISOString();
    uploadingRef.current = true;
    setIsUploading(true);

    try {
      let prepareData;
      try {
        prepareData = await preparePhotoUpload(currentConnector, file);
      } catch {
        winiMsg.showSnackbar('사진 업로드 준비 중 오류가 발생했습니다.');
        return null;
      }

      if (prepareData?.result !== 'SUCCESS' || !prepareData?.data?.uploadUrl) {
        winiMsg.showSnackbar(
          prepareData?.message || '사진 업로드 준비에 실패했습니다.',
        );
        return null;
      }

      const { uploadUrl, fileId } = prepareData.data;

      try {
        await uploadPhotoToPresignedUrl(uploadUrl, file);
      } catch (error) {
        const message =
          error?.code === PHOTO_UPLOAD_ERROR_CODE.PROXY_REQUIRED
            ? error.message
            : '사진 업로드 중 오류가 발생했습니다.';
        winiMsg.showSnackbar(message);
        return null;
      }

      return {
        photoFileId: fileId,
        capturedAt,
        uploadedAt: winiDate.now().toISOString(),
      };
    } finally {
      uploadingRef.current = false;
      setIsUploading(false);
    }
  }, []);

  return { capture, isUploading };
};
