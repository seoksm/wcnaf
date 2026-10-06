import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { useMyAcknowledgements, MyAcknowledgementList, MyAcknowledgementDetail } from '@/features/acknowledgement/myApproval';

/** S-440 확인서 승인 (모바일웹) */
export const MyAcknowledgementPage = () => {
  const ctl = useMyAcknowledgements();

  return (
    <WiniFormNormal>
      {ctl.isLoading && (
        <WiniBox className="p-6 text-center">
          <WiniTypography variant="span" className="text-text-sub">불러오는 중...</WiniTypography>
        </WiniBox>
      )}

      {!ctl.isLoading && ctl.mode === 'list' && (
        <MyAcknowledgementList list={ctl.list} onSelect={ctl.openDetail} />
      )}

      {!ctl.isLoading && ctl.mode === 'detail' && (
        <MyAcknowledgementDetail
          detail={ctl.detail}
          isLoading={ctl.isDetailLoading}
          agreed={ctl.agreed}
          onAgreedChange={ctl.setAgreed}
          isApproving={ctl.isApproving}
          onApprove={ctl.approve}
          onBack={ctl.backToList}
        />
      )}
    </WiniFormNormal>
  );
};

export default MyAcknowledgementPage;
