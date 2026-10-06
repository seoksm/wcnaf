import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniButton } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';
import { LoanListGrid, LoanListSearch, LoanListPager } from '@/features/loan/list';
import { AdminBorrowDialog } from '@/features/loan/adminBorrow';
import { useLoanManagementPage } from '../model/useLoanManagementPage';

/** S-410 대여 현황 - 관리자 대행 등록(S-412)은 이 화면에서 모달로 진입한다 */
export const LoanManagementPage = () => {
  const ctl = useLoanManagementPage();
  const { adminBorrow } = ctl;

  return (
    <WiniFormNormal>
      <LoanListSearch
        searchData={ctl.searchData}
        onSearchChange={ctl.onSearchChange}
        onSearch={ctl.onSearch}
        isLoading={ctl.isLoading}
        extraActions={winiCom.checkMenuAut(
          'insert',
          <WiniButton ui="line" onClick={adminBorrow.openDialog}>대여 처리(관리자 대행)</WiniButton>,
        )}
      />

      <LoanListGrid rowData={ctl.loanList} isLoading={ctl.isLoading} memberNameById={ctl.memberNameById} />
      <LoanListPager pageInfo={ctl.pageInfo} onPageChange={ctl.onPageChange} isLoading={ctl.isLoading} />

      <AdminBorrowDialog
        open={adminBorrow.open}
        form={adminBorrow.form}
        userList={adminBorrow.userList}
        isResolving={adminBorrow.isResolving}
        isSubmitting={adminBorrow.isSubmitting}
        onClose={adminBorrow.closeDialog}
        onChange={adminBorrow.handleChange}
        onResolveAsset={adminBorrow.resolveAsset}
        onSubmit={adminBorrow.submit}
      />
    </WiniFormNormal>
  );
};

export default LoanManagementPage;
