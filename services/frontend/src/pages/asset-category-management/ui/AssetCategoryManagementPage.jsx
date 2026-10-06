import React from 'react';
import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import {
  WiniBox,
  WiniGridItem,
  WiniGridLayout,
  WiniTypography,
} from '@/shared/ui/wini';
import {
  AssetCategoryListSearch,
  AssetCategoryListGrid,
} from '@/features/assetCategory/list';
import { AssetCategoryEditor } from '@/features/assetCategory/manage';

import { useAssetCategoryManagementPage } from '../model/useAssetCategoryManagementPage';

export const AssetCategoryManagementPage = () => {
  const ctl = useAssetCategoryManagementPage();
  const isBusy = ctl.isLoading || ctl.editor.isSubmitting;

  return (
    <WiniFormNormal>
      <WiniGridLayout container>
        <WiniGridItem size={{ md: 6, xs: 12 }}>
          <AssetCategoryListSearch
            searchData={ctl.searchData}
            onSearchChange={ctl.onSearchChange}
            onSearch={ctl.onSelect}
            isLoading={isBusy}
          />
        </WiniGridItem>
      </WiniGridLayout>

      {ctl.error && (
        <WiniBox ui="info" className="mb-2">
          <WiniTypography variant="span" className="text-sm text-red-500">
            자산 종류 목록을 불러오지 못했습니다. 잠시 후 다시 조회해주세요.
          </WiniTypography>
        </WiniBox>
      )}

      <WiniGridLayout container className="flex justify-between">
        <WiniGridItem size={{ md: 8.4, xs: 12 }}>
          <AssetCategoryListGrid
            rowData={ctl.dataListView}
            isLoading={ctl.isLoading}
            disabled={isBusy}
            onRowSelect={ctl.editor.setSelected}
          />
        </WiniGridItem>

        <WiniGridItem size={{ md: 3.4, xs: 12 }}>
          <AssetCategoryEditor
            formData={ctl.editor.formData}
            categoryCodeDisabled={ctl.editor.categoryCodeDisabled}
            deletable={ctl.editor.deletable}
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

export default AssetCategoryManagementPage;
