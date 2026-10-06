import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { useMyLoans, MyLoanList, ReturnConditionDialog } from '@/features/loan/myLoans';
import { LoanScanView } from '@/features/loan/scan';

/** S-421 내 대여 자산 (모바일웹) */
export const MyLoanPage = () => {
  const ctl = useMyLoans();

  return (
    <WiniFormNormal>
      {ctl.isLoading && (
        <WiniBox className="p-6 text-center">
          <WiniTypography variant="span" className="text-text-sub">불러오는 중...</WiniTypography>
        </WiniBox>
      )}

      {!ctl.isLoading && ctl.mode === 'scan' && (
        <LoanScanView
          title="QR 스캔으로 반납"
          subtitle="반납할 자산에 부착된 QR 라벨을 카메라에 비춰주세요."
          onDecoded={ctl.onScanDecoded}
          lastFeedback={ctl.lastFeedback}
          onBack={ctl.backToList}
          paused={!!ctl.pendingReturn}
        />
      )}

      {!ctl.isLoading && ctl.mode === 'list' && (
        <MyLoanList loans={ctl.loans} isActing={ctl.isActing} onStartReturnScan={ctl.startReturnScan} onExtend={ctl.extend} />
      )}

      <ReturnConditionDialog
        pendingReturn={ctl.pendingReturn}
        isActing={ctl.isActing}
        onCancel={ctl.cancelReturn}
        onConfirm={ctl.confirmReturn}
      />
    </WiniFormNormal>
  );
};

export default MyLoanPage;
