import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniGridItem, WiniGridLayout } from '@/shared/ui/wini';
import { VendorListSearch, VendorListGrid } from '@/features/vendor/list';
import { VendorEditor } from '@/features/vendor/manage';
import { useVendorManagementPage } from '../model/useVendorManagementPage';

/** S-540 공급사 관리 - 렌탈·라이선스 공용 */
export const VendorManagementPage = () => {
  const ctl = useVendorManagementPage();
  const isBusy = ctl.isLoading || ctl.editor.isSubmitting;

  return (
    <WiniFormNormal>
      <WiniGridLayout container>
        <WiniGridItem size={{ md: 6, xs: 12 }}>
          <VendorListSearch
            searchData={ctl.searchData}
            onSearchChange={ctl.onSearchChange}
            onSearch={ctl.onSearch}
            isLoading={isBusy}
          />
        </WiniGridItem>
      </WiniGridLayout>

      <WiniGridLayout container className="flex justify-between">
        <WiniGridItem size={{ md: 8.4, xs: 12 }}>
          <VendorListGrid rowData={ctl.list} isLoading={ctl.isLoading} disabled={isBusy} onRowSelect={ctl.editor.setSelected} />
        </WiniGridItem>

        <WiniGridItem size={{ md: 3.4, xs: 12 }}>
          <VendorEditor
            formData={ctl.editor.formData}
            onChange={ctl.editor.handleChange}
            onReset={ctl.editor.reset}
            onCreate={ctl.editor.handleCreate}
            onUpdate={ctl.editor.handleUpdate}
            onDelete={ctl.editor.handleDelete}
            disabled={isBusy}
            isSubmitting={ctl.editor.isSubmitting}
            submittingAction={ctl.editor.submittingAction}
          />
        </WiniGridItem>
      </WiniGridLayout>
    </WiniFormNormal>
  );
};

export default VendorManagementPage;
