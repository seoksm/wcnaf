import { useCallback, useRef, useState } from 'react';
import QRCode from 'qrcode';
import { winiMsg } from '@/shared/model';

/**
 * 유형자산 QR 라벨 발행 (S-216)
 * 서버 PDF 대신 브라우저 인쇄 미리보기로 구현 - 라벨지 mm단위 정밀 정렬·출력이력은 이번 범위에서 제외.
 */
export const useQrLabels = () => {
  const [labels, setLabels] = useState([]);
  const [isGenerating, setIsGenerating] = useState(false);
  const generatingRef = useRef(false);

  const printLabels = useCallback(async (checkedRows) => {
    if (generatingRef.current) return;
    if (!checkedRows || checkedRows.length === 0) {
      winiMsg.showAlert('QR 라벨을 발행할 자산을 선택해주세요.');
      return;
    }

    generatingRef.current = true;
    setIsGenerating(true);
    try {
      const generated = await Promise.all(
        checkedRows.map(async (row) => {
          // Q-18: 자산코드가 아니라 UUID 토큰(자산 ID 그 자체)을 인코딩 - 순번 나열로 전체 자산 열거 방지
          const scanUrl = `${window.location.origin}/asset-scan/${row.tangibleAssetId}`;
          const qrDataUrl = await QRCode.toDataURL(scanUrl, { margin: 1, width: 160 });
          return {
            tangibleAssetId: row.tangibleAssetId,
            assetCode: row.assetCode,
            assetName: row.assetName,
            qrDataUrl,
          };
        }),
      );

      setLabels(generated);
      // 라벨 이미지가 실제로 그려진 뒤 인쇄창을 띄우기 위해 다음 페인트까지 대기
      requestAnimationFrame(() => requestAnimationFrame(() => window.print()));
    } catch {
      winiMsg.showSnackbar('QR 라벨 생성 중 오류가 발생했습니다.');
    } finally {
      generatingRef.current = false;
      setIsGenerating(false);
    }
  }, []);

  return { labels, isGenerating, printLabels };
};
