import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { useAvailableAssets, AvailableAssetList } from '@/features/loan/available';
import { LoanScanView } from '@/features/loan/scan';

/** S-420 대여 가능 자산 (모바일웹) */
export const LoanAvailablePage = () => {
  const ctl = useAvailableAssets();

  return (
    <WiniFormNormal>
      {ctl.mode === 'scan' ? (
        <LoanScanView
          title="QR 스캔으로 대여"
          subtitle="대여할 자산에 부착된 QR 라벨을 카메라에 비춰주세요."
          onDecoded={ctl.onScanDecoded}
          lastFeedback={ctl.lastFeedback}
          onBack={ctl.backToList}
          paused={ctl.isBorrowing}
        />
      ) : (
        <AvailableAssetList assets={ctl.assets} isLoading={ctl.isLoading} onStartScan={ctl.startScan} />
      )}
    </WiniFormNormal>
  );
};

export default LoanAvailablePage;
