import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { useMyTickets, MyTicketList, MyTicketRegisterDialog, MyTicketCommentDialog } from '@/features/ticket/myTickets';

/** S-610 티켓 등록 · S-611 내 티켓 (모바일웹) */
export const MyTicketPage = () => {
  const ctl = useMyTickets();

  return (
    <WiniFormNormal>
      {ctl.isLoading ? (
        <WiniBox className="p-6 text-center">
          <WiniTypography variant="span" className="text-text-sub">불러오는 중...</WiniTypography>
        </WiniBox>
      ) : (
        <MyTicketList tickets={ctl.tickets} onOpenRegister={ctl.openRegister} onSelectTicket={ctl.openTicket} />
      )}

      <MyTicketRegisterDialog
        open={ctl.registerOpen}
        form={ctl.form}
        isResolving={ctl.isResolving}
        isActing={ctl.isActing}
        onClose={ctl.closeRegister}
        onChange={ctl.handleFormChange}
        onResolveAsset={ctl.resolveAsset}
        onSubmit={ctl.submitRegister}
      />

      <MyTicketCommentDialog
        ticket={ctl.selectedTicket}
        comments={ctl.comments}
        newComment={ctl.newComment}
        isActing={ctl.isActing}
        onNewCommentChange={ctl.setNewComment}
        onSubmitComment={ctl.submitComment}
        onClose={ctl.closeTicket}
      />
    </WiniFormNormal>
  );
};

export default MyTicketPage;
