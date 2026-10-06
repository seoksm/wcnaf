import React from 'react';
import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniGridItem, WiniGridLayout } from '@/shared/ui/wini';
import {
  DepreciationStatusSearch,
  DepreciationStatusSummary,
  DepreciationStatusGrid,
} from '@/features/depreciation/status';
import { ScheduleDialog } from '@/features/depreciation/schedule';
import {
  ConfirmationBar,
  DepreciationLogDialog,
} from '@/features/depreciation/confirmation';

import { useDepreciationManagementPage } from '../model/useDepreciationManagementPage';

export const DepreciationManagementPage = () => {
  const ctl = useDepreciationManagementPage();

  return (
    <WiniFormNormal>
      <WiniGridLayout container>
        <WiniGridItem size={{ md: 8, xs: 12 }}>
          <DepreciationStatusSearch
            searchData={ctl.searchData}
            onSearchChange={ctl.onSearchChange}
            onSearch={ctl.onSelect}
            isLoading={ctl.isLoading}
          />
        </WiniGridItem>
      </WiniGridLayout>

      <ConfirmationBar
        confirmed={!!ctl.summary?.confirmed}
        releaseReason={ctl.confirmation.releaseReason}
        onReleaseReasonChange={ctl.confirmation.setReleaseReason}
        onConfirm={ctl.confirmation.handleConfirm}
        onRelease={ctl.confirmation.handleRelease}
        onOpenLog={ctl.confirmation.openLogDialog}
        isSubmitting={ctl.confirmation.isSubmitting}
        isLogLoading={ctl.confirmation.isLogLoading}
        disabled={ctl.isLoading}
      />

      <DepreciationStatusSummary summary={ctl.summary} />

      <WiniGridLayout container>
        <WiniGridItem xs={12}>
          <DepreciationStatusGrid
            rowData={ctl.rows}
            onRowSelect={ctl.onRowSelect}
            isLoading={ctl.isLoading}
          />
        </WiniGridItem>
      </WiniGridLayout>

      <ScheduleDialog
        open={ctl.schedule.open}
        asset={ctl.schedule.asset}
        schedule={ctl.schedule.schedule}
        isLoading={ctl.schedule.isLoading}
        onClose={ctl.schedule.closeSchedule}
      />

      <DepreciationLogDialog
        open={ctl.confirmation.logDialogOpen}
        log={ctl.confirmation.log}
        isLoading={ctl.confirmation.isLogLoading}
        onClose={ctl.confirmation.closeLogDialog}
      />
    </WiniFormNormal>
  );
};

export default DepreciationManagementPage;
