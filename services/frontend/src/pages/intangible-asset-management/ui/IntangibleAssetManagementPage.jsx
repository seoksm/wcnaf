import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniButton } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';
import { IntangibleAssetListGrid, IntangibleAssetListSearch, IntangibleAssetListPager } from '@/features/intangibleAsset/list';
import { IntangibleAssetDialog } from '@/features/intangibleAsset/manage';
import { IntangibleAssetDetailDialog } from '@/features/intangibleAsset/detail';
import { useIntangibleAssetManagementPage } from '../model/useIntangibleAssetManagementPage';

/** S-500 무형자산 목록 - S-501(등록/수정)·S-502(상세·갱신)는 이 화면 안에서 모달로 진입한다 */
export const IntangibleAssetManagementPage = () => {
  const ctl = useIntangibleAssetManagementPage();
  const { dialog, detail } = ctl;

  return (
    <WiniFormNormal>
      <IntangibleAssetListSearch
        searchData={ctl.searchData}
        onSearchChange={ctl.onSearchChange}
        onSearch={ctl.onSearch}
        isLoading={ctl.isLoading}
        extraActions={winiCom.checkMenuAut(
          'insert',
          <WiniButton ui="line" onClick={dialog.openCreate}>무형자산 등록</WiniButton>,
        )}
      />

      <IntangibleAssetListGrid rowData={ctl.list} isLoading={ctl.isLoading} onRowSelect={ctl.onRowSelect} />
      <IntangibleAssetListPager pageInfo={ctl.pageInfo} onPageChange={ctl.onPageChange} />

      <IntangibleAssetDialog
        open={dialog.open}
        form={dialog.form}
        userList={dialog.userList}
        isSubmitting={dialog.isSubmitting}
        onClose={dialog.closeDialog}
        onChange={dialog.handleChange}
        onDateChange={dialog.handleDateChange}
        onSubmit={dialog.submit}
      />

      <IntangibleAssetDetailDialog
        row={detail.selectedRow}
        actionLog={detail.actionLog}
        isLoading={detail.isLoading}
        isActing={detail.isActing}
        renewForm={detail.renewForm}
        onRenewFormChange={detail.handleRenewFormChange}
        onSubmitRenew={detail.submitRenew}
        onEdit={ctl.onEditFromDetail}
        onDelete={detail.removeAsset}
        onClose={detail.closeDetail}
      />
    </WiniFormNormal>
  );
};

export default IntangibleAssetManagementPage;
