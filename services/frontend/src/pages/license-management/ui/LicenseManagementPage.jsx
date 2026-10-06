import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniButton } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';
import { LicenseListGrid, LicenseListSearch, LicenseListPager } from '@/features/license/list';
import { LicenseDialog } from '@/features/license/manage';
import { LicenseDetailDialog } from '@/features/license/detail';
import { useLicenseManagementPage } from '../model/useLicenseManagementPage';

/** S-530 라이선스 목록 - S-531(등록/수정)·S-532~533(상세·구매내역·배정·포함SW)는 이 화면 안에서 모달로 진입한다 */
export const LicenseManagementPage = () => {
  const ctl = useLicenseManagementPage();
  const { dialog, detail } = ctl;

  return (
    <WiniFormNormal>
      <LicenseListSearch
        searchData={ctl.searchData}
        onSearchChange={ctl.onSearchChange}
        onSearch={ctl.onSearch}
        isLoading={ctl.isLoading}
        extraActions={winiCom.checkMenuAut(
          'insert',
          <WiniButton ui="line" onClick={dialog.openCreate}>라이선스 등록</WiniButton>,
        )}
      />

      <LicenseListGrid rowData={ctl.list} isLoading={ctl.isLoading} onRowSelect={ctl.onRowSelect} />
      <LicenseListPager pageInfo={ctl.pageInfo} onPageChange={ctl.onPageChange} />

      <LicenseDialog
        open={dialog.open}
        form={dialog.form}
        isSubmitting={dialog.isSubmitting}
        onClose={dialog.closeDialog}
        onChange={dialog.handleChange}
        onSubmit={dialog.submit}
      />

      <LicenseDetailDialog
        row={detail.selectedRow}
        purchaseRecords={detail.purchaseRecords}
        assignedUsers={detail.assignedUsers}
        includedSoftware={detail.includedSoftware}
        vendorList={detail.vendorList}
        userList={detail.userList}
        isLoading={detail.isLoading}
        isActing={detail.isActing}
        newRecord={detail.newRecord}
        newAssign={detail.newAssign}
        newSoftwareName={detail.newSoftwareName}
        userNameById={detail.userNameById}
        onNewRecordChange={detail.handleNewRecordChange}
        onNewRecordDateChange={detail.handleNewRecordDateChange}
        onAddPurchaseRecord={detail.addPurchaseRecord}
        onNewAssignChange={detail.handleNewAssignChange}
        onAssignUser={detail.assignUser}
        onReleaseUser={detail.releaseUser}
        onNewSoftwareNameChange={detail.setNewSoftwareName}
        onAddIncludedSoftware={detail.addIncludedSoftware}
        onRemoveIncludedSoftware={detail.removeIncludedSoftware}
        onEdit={ctl.onEditFromDetail}
        onRemoveLicense={detail.removeLicense}
        onClose={detail.closeDetail}
      />
    </WiniFormNormal>
  );
};

export default LicenseManagementPage;
