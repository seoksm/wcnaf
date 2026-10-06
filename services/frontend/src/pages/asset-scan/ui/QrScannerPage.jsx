import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { WiniFormEmpty } from '@/shared/ui/blocks/form-layout';
import { QrScannerView } from '@/features/tangibleAsset/qr-scan';

/**
 * QR 스캔 조회 - 모바일웹 공용 진입점 (S-217)
 */
export const QrScannerPage = () => {
  const navigate = useNavigate();

  const handleDecoded = useCallback(
    (tangibleAssetId) => {
      navigate(`/asset-scan/${tangibleAssetId}`);
    },
    [navigate],
  );

  return (
    <WiniFormEmpty>
      <QrScannerView onDecoded={handleDecoded} />
    </WiniFormEmpty>
  );
};

export default QrScannerPage;
