import React from 'react';
import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniButton, WiniGridItem, WiniGridLayout, WiniTypography } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';
import { InventoryListGrid, InventoryListPager } from '@/features/inventory/list';
import { CreateMemberInventoryDialog, CreateAdminInventoryDialog } from '@/features/inventory/create';
import { InventoryProgressSummary, InventoryResultsGrid, InventoryResultActionsPanel } from '@/features/inventory/detail';
import { InventoryReportView } from '@/features/inventory/report';
import { InventoryScheduleList } from '@/features/inventory/schedule';

import { useInventoryManagementPage } from '../model/useInventoryManagementPage';

/**
 * S-300 전수조사 목록 · S-301/302 생성(임직원형/관리자형) · S-303 진행 현황 · S-304 검수 승인/반려
 * (관리자 대체 확인 경유) · S-305 리포트 · S-306 반복 시행 스케줄(템플릿 복제) · S-308 종결 처리.
 */
export const InventoryManagementPage = () => {
  const ctl = useInventoryManagementPage();
  const detail = ctl.detail;

  return (
    <WiniFormNormal>
      <WiniGridLayout container className="flex justify-between">
        <WiniGridItem xs={12}>
          <WiniBox className="mb-2 flex justify-end gap-2">
            <WiniButton ui="lineGray" onClick={ctl.schedule.togglePanel}>반복 시행 스케줄</WiniButton>
            {winiCom.checkMenuAut(
              'insert',
              <WiniButton ui="lineGray" onClick={ctl.createAdmin.openDialog}>
                전수조사 생성 (관리자형)
              </WiniButton>,
            )}
            {winiCom.checkMenuAut(
              'insert',
              <WiniButton onClick={ctl.createMember.openDialog}>전수조사 생성 (임직원형)</WiniButton>,
            )}
          </WiniBox>

          {ctl.schedule.open && (
            <WiniBox className="mb-3 rounded border border-solid border-gray-200 bg-gray-50 p-3">
              <InventoryScheduleList
                schedules={ctl.schedule.schedules}
                isLoading={ctl.schedule.isLoading}
                error={ctl.schedule.error}
                isCloning={ctl.schedule.isCloning}
                onClone={ctl.schedule.cloneFrom}
              />
            </WiniBox>
          )}

          {ctl.listError && (
            <WiniBox ui="info" className="mb-2">
              <WiniTypography variant="span" className="text-sm text-red-500">
                {ctl.listError}
              </WiniTypography>
            </WiniBox>
          )}

          <InventoryListGrid rowData={ctl.inventoryList} isLoading={ctl.isListLoading} onRowSelect={ctl.onSelectInventory} />
          <InventoryListPager pageInfo={ctl.pageInfo} onPageChange={ctl.onPageChange} isLoading={ctl.isListLoading} />
        </WiniGridItem>
      </WiniGridLayout>

      {detail.inventoryId && (
        <WiniGridLayout container className="mt-4 flex justify-between">
          <WiniGridItem xs={12}>
            <WiniBox className="mb-2 flex items-center justify-between">
              <WiniTypography variant="span" className="block text-base font-bold text-text-main">
                진행 현황 · 검수 · 종결 처리
              </WiniTypography>
              <WiniButton ui="lineGray" onClick={() => ctl.report.openReport(detail.inventoryId)}>
                리포트 보기
              </WiniButton>
            </WiniBox>
            {detail.error && (
              <WiniBox ui="info" className="mb-2">
                <WiniTypography variant="span" className="text-sm text-red-500">
                  {detail.error}
                </WiniTypography>
              </WiniBox>
            )}
            <InventoryProgressSummary progress={detail.progress} />
          </WiniGridItem>

          <WiniGridItem size={{ md: 8.4, xs: 12 }}>
            <InventoryResultsGrid
              statusFilter={detail.statusFilter}
              onStatusFilterChange={detail.onStatusFilterChange}
              rowData={detail.results}
              isLoading={detail.isLoading}
              onRowSelect={detail.setSelectedRow}
              onCheckSelectionChange={detail.onCheckSelectionChange}
            />
          </WiniGridItem>

          <WiniGridItem size={{ md: 3.4, xs: 12 }}>
            <InventoryResultActionsPanel
              selectedRow={detail.selectedRow}
              checkedCount={detail.checkedRows.length}
              anomalyForm={detail.anomalyForm}
              onAnomalyFormChange={detail.onAnomalyFormChange}
              closeForm={detail.closeForm}
              onCloseFormChange={detail.onCloseFormChange}
              isActing={detail.isActing}
              onAdminConfirm={detail.adminConfirm}
              onAdminAnomaly={detail.adminAnomaly}
              onApprove={detail.approve}
              onReject={detail.reject}
              onCloseOne={detail.closeOne}
              onCloseBulk={detail.closeBulk}
              onFinalize={detail.finalizeInventory}
            />
          </WiniGridItem>
        </WiniGridLayout>
      )}

      <InventoryReportView
        open={ctl.report.open}
        report={ctl.report.report}
        isLoading={ctl.report.isLoading}
        isDownloading={ctl.report.isDownloading}
        onClose={ctl.report.closeReport}
        onDownloadExcel={ctl.report.downloadExcel}
      />

      <CreateMemberInventoryDialog
        open={ctl.createMember.open}
        formData={ctl.createMember.formData}
        userList={ctl.createMember.userList}
        preview={ctl.createMember.preview}
        isPreviewing={ctl.createMember.isPreviewing}
        isSubmitting={ctl.createMember.isSubmitting}
        onChange={ctl.createMember.onChange}
        onToggle={ctl.createMember.onToggle}
        onRunPreview={ctl.createMember.runPreview}
        onClose={ctl.createMember.closeDialog}
        onSubmit={ctl.createMember.submit}
      />

      <CreateAdminInventoryDialog
        open={ctl.createAdmin.open}
        formData={ctl.createAdmin.formData}
        userList={ctl.createAdmin.userList}
        preview={ctl.createAdmin.preview}
        isPreviewing={ctl.createAdmin.isPreviewing}
        isSubmitting={ctl.createAdmin.isSubmitting}
        onChange={ctl.createAdmin.onChange}
        onToggle={ctl.createAdmin.onToggle}
        onClose={ctl.createAdmin.closeDialog}
        onSubmit={ctl.createAdmin.submit}
      />
    </WiniFormNormal>
  );
};

export default InventoryManagementPage;
