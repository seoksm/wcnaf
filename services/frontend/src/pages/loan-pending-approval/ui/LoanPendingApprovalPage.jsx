import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { PendingGrid, ApprovalActionBar } from '@/features/loan/pendingApproval';
import { useLoanPendingApprovalPage } from '../model/useLoanPendingApprovalPage';

/** S-411 대여 승인대기 - Q-39 승인 옵션이 켜져 있을 때 승인을 기다리는 대여 목록 */
export const LoanPendingApprovalPage = () => {
  const ctl = useLoanPendingApprovalPage();

  return (
    <WiniFormNormal>
      <PendingGrid
        rowData={ctl.pendingList}
        isLoading={ctl.isLoading}
        memberNameById={ctl.memberNameById}
        onRowSelect={ctl.onRowSelect}
      />
      <ApprovalActionBar
        selectedRow={ctl.selectedRow}
        isActing={ctl.isActing}
        onApprove={ctl.approve}
        onOpenReject={ctl.openRejectDialog}
        rejectDialogOpen={ctl.rejectDialogOpen}
        rejectReason={ctl.rejectReason}
        onRejectReasonChange={ctl.setRejectReason}
        onCloseReject={ctl.closeRejectDialog}
        onSubmitReject={ctl.submitReject}
      />
    </WiniFormNormal>
  );
};

export default LoanPendingApprovalPage;
