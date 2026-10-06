import React from 'react';
import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import {
  WiniBox,
  WiniGridItem,
  WiniGridLayout,
  WiniTypography,
} from '@/shared/ui/wini';
import {
  DisposalAssetListSearch,
  DisposalAssetListGrid,
  DisposalAssetListPager,
} from '@/features/disposalAsset/list';
import { DisposalActionPanel } from '@/features/disposalAsset/actions';

import { useDisposalAssetManagementPage } from '../model/useDisposalAssetManagementPage';

/**
 * 불용자산 목록(S-240) · 복귀(S-240) · 처분 처리(S-242) - 목록에서 선택한 자산에 대해
 * 우측 패널에서 복귀/처분을 처리한다. 불용 처리(S-241) 자체는 유형자산 관리 화면에서 시작한다.
 */
export const DisposalAssetManagementPage = () => {
  const ctl = useDisposalAssetManagementPage();

  return (
    <WiniFormNormal>
      <WiniGridLayout container>
        <WiniGridItem size={{ md: 6, xs: 12 }}>
          <DisposalAssetListSearch
            searchData={ctl.searchData}
            onSearchChange={ctl.onSearchChange}
            onSearch={ctl.onSelect}
            isLoading={ctl.isListLoading || ctl.isActionRunning}
          />
        </WiniGridItem>
      </WiniGridLayout>

      {ctl.listError && (
        <WiniBox ui="info" className="mb-2">
          <WiniTypography variant="span" className="text-sm text-red-500">
            불용자산 목록을 불러오지 못했습니다. 잠시 후 다시 조회해주세요.
          </WiniTypography>
        </WiniBox>
      )}

      <WiniGridLayout container className="flex justify-between">
        <WiniGridItem size={{ md: 8.4, xs: 12 }}>
          <DisposalAssetListGrid
            rowData={ctl.disposalAssetList}
            isLoading={ctl.isListLoading}
            onRowSelect={ctl.onRowSelect}
          />
          <DisposalAssetListPager
            pageInfo={ctl.pageInfo}
            onPageChange={ctl.onPageChange}
            isLoading={ctl.isListLoading || ctl.isActionRunning}
          />
        </WiniGridItem>

        <WiniGridItem size={{ md: 3.4, xs: 12 }}>
          <DisposalActionPanel
            selectedRow={ctl.selectedRow}
            disposeData={ctl.actions.disposeData}
            onDisposeChange={ctl.actions.onDisposeChange}
            isDisposing={ctl.actions.isDisposing}
            onDispose={ctl.actions.dispose}
            isRestoring={ctl.actions.isRestoring}
            onRestore={ctl.actions.restore}
            isActing={ctl.isActionRunning}
          />
        </WiniGridItem>
      </WiniGridLayout>
    </WiniFormNormal>
  );
};

export default DisposalAssetManagementPage;
