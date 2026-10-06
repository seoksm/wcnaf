import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniGridItem, WiniGridLayout } from '@/shared/ui/wini';
import { SoftwareListSearch, SoftwareListGrid } from '@/features/software/list';
import { SoftwareEditor } from '@/features/software/manage';
import { useSoftwareManagementPage } from '../model/useSoftwareManagementPage';

/** S-520 소프트웨어 마스터 관리 - 라이선스의 "포함 SW"는 이 마스터를 R2 자동완성으로 참조한다 */
export const SoftwareManagementPage = () => {
  const ctl = useSoftwareManagementPage();
  const isBusy = ctl.isLoading || ctl.editor.isSubmitting;

  return (
    <WiniFormNormal>
      <WiniGridLayout container>
        <WiniGridItem size={{ md: 6, xs: 12 }}>
          <SoftwareListSearch
            searchData={ctl.searchData}
            onSearchChange={ctl.onSearchChange}
            onSearch={ctl.onSearch}
            isLoading={isBusy}
          />
        </WiniGridItem>
      </WiniGridLayout>

      <WiniGridLayout container className="flex justify-between">
        <WiniGridItem size={{ md: 8.4, xs: 12 }}>
          <SoftwareListGrid rowData={ctl.list} isLoading={ctl.isLoading} disabled={isBusy} onRowSelect={ctl.editor.setSelected} />
        </WiniGridItem>

        <WiniGridItem size={{ md: 3.4, xs: 12 }}>
          <SoftwareEditor
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

export default SoftwareManagementPage;
