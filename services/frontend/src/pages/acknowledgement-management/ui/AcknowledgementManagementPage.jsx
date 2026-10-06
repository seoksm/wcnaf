import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniButton } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';
import { AcknowledgementListGrid, AcknowledgementListSearch, AcknowledgementListPager } from '@/features/acknowledgement/list';
import { RequestAcknowledgementDialog } from '@/features/acknowledgement/request';
import { AcknowledgementDetailDialog } from '@/features/acknowledgement/detail';
import { useAcknowledgementManagementPage } from '../model/useAcknowledgementManagementPage';

/** S-431 확인서 현황 - S-430(요청)·S-432(상세·담당자승인)는 이 화면에서 모달로 진입한다 */
export const AcknowledgementManagementPage = () => {
  const ctl = useAcknowledgementManagementPage();
  const { requestDialog, detail } = ctl;

  return (
    <WiniFormNormal>
      <AcknowledgementListSearch
        searchData={ctl.searchData}
        onSearchChange={ctl.onSearchChange}
        onSearch={ctl.onSearch}
        isLoading={ctl.isLoading}
        extraActions={winiCom.checkMenuAut(
          'insert',
          <WiniButton ui="line" onClick={requestDialog.openDialog}>확인서 요청</WiniButton>,
        )}
      />

      <AcknowledgementListGrid
        rowData={ctl.list}
        isLoading={ctl.isLoading}
        memberNameById={ctl.memberNameById}
        onRowSelect={detail.openDetail}
      />
      <AcknowledgementListPager pageInfo={ctl.pageInfo} onPageChange={ctl.onPageChange} isLoading={ctl.isLoading} />

      <RequestAcknowledgementDialog
        open={requestDialog.open}
        form={requestDialog.form}
        userList={requestDialog.userList}
        isResolving={requestDialog.isResolving}
        isSubmitting={requestDialog.isSubmitting}
        onClose={requestDialog.closeDialog}
        onChange={requestDialog.handleChange}
        onResolveAsset={requestDialog.resolveAsset}
        onSubmit={requestDialog.submit}
      />

      <AcknowledgementDetailDialog
        open={!!detail.selectedId}
        detail={detail.detail}
        isLoading={detail.isLoading}
        isActing={detail.isActing}
        approveForm={detail.approveForm}
        onApproveFormChange={detail.handleApproveFormChange}
        onSubmitApprove={detail.submitManagerApprove}
        cancelReason={detail.cancelReason}
        onCancelReasonChange={detail.setCancelReason}
        onSubmitCancel={detail.submitCancel}
        onClose={detail.closeDetail}
      />
    </WiniFormNormal>
  );
};

export default AcknowledgementManagementPage;
