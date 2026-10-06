import { useNavigate } from 'react-router-dom';
import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import {
  WiniBox,
  WiniButton,
  WiniGridItem,
  WiniGridLayout,
  WiniTypography,
} from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';
import {
  TangibleAssetListSearch,
  TangibleAssetListGrid,
  TangibleAssetListPager,
} from '@/features/tangibleAsset/list';
import { TangibleAssetEditor } from '@/features/tangibleAsset/manage';
import { AssignmentHistoryPanel } from '@/features/tangibleAsset/assignment';
import { DisuseDialog } from '@/features/tangibleAsset/disuse';
import {
  BulkActionBar,
  DuplicateDialog,
  BatchUpdateDialog,
} from '@/features/tangibleAsset/bulk';
import { QrLabelPrintArea } from '@/features/tangibleAsset/qrLabel';
import { ExcelUpsertDialog } from '@/features/tangibleAsset/excelUpsert';
import {
  AssetHistoryPanel,
  ActivityLogDialog,
} from '@/features/tangibleAsset/history';

import { useTangibleAssetManagementPage } from '../model/useTangibleAssetManagementPage';

export const TangibleAssetManagementPage = () => {
  const ctl = useTangibleAssetManagementPage();
  const navigate = useNavigate();
  const isOptionsUnavailable = ctl.isOptionsLoading || !ctl.isOptionsReady;

  return (
    <WiniFormNormal>
      <WiniGridLayout container>
        <WiniGridItem size={{ md: 6, xs: 12 }}>
          <TangibleAssetListSearch
            searchData={ctl.searchData}
            onSearchChange={ctl.onSearchChange}
            onSearch={ctl.onSelect}
            isLoading={ctl.isListLoading}
            disabled={ctl.editor.isSaving}
            onOpenQrScan={() => navigate('/asset-scan')}
          />
        </WiniGridItem>
      </WiniGridLayout>

      {ctl.listError && (
        <WiniBox ui="info" className="mb-2">
          <WiniTypography variant="span" className="text-sm text-red-500">
            {ctl.listError}
          </WiniTypography>
        </WiniBox>
      )}

      {ctl.optionsError && (
        <WiniBox
          ui="info"
          className="mb-2 flex flex-wrap items-center justify-between gap-2"
        >
          <WiniTypography variant="span" className="text-sm text-red-500">
            {ctl.optionsError} 등록·수정 및 일괄 변경을 사용할 수 없습니다.
          </WiniTypography>
          <WiniButton
            ui="lineGray"
            onClick={ctl.reloadOptions}
            loading={ctl.isOptionsLoading}
            disabled={ctl.isOptionsLoading}
          >
            옵션 다시 조회
          </WiniButton>
        </WiniBox>
      )}

      <WiniGridLayout container className="flex justify-between">
        <WiniGridItem size={{ md: 8.4, xs: 12 }}>
          <BulkActionBar
            checkedCount={ctl.checkedRows.length}
            onDuplicate={ctl.bulk.openDuplicate}
            onBatchUpdate={ctl.bulk.openBatch}
            onPrintQrLabels={ctl.printQrLabels}
            onOpenExcelUpsert={ctl.excelUpsert.openDialog}
            onOpenActivityLog={ctl.activityLog.openDialog}
            isGeneratingQr={ctl.qrLabel.isGenerating}
            isListLoading={ctl.isListLoading}
            isBatchDisabled={isOptionsUnavailable}
          />
          <TangibleAssetListGrid
            rowData={ctl.dataListView}
            isLoading={ctl.isListLoading}
            disabled={ctl.isListLoading}
            onRowSelect={ctl.editor.setSelected}
            onCheckSelectionChange={ctl.onCheckSelectionChange}
          />
          <TangibleAssetListPager
            pageInfo={ctl.pageInfo}
            onPageChange={ctl.onPageChange}
            isLoading={ctl.isListLoading}
          />
        </WiniGridItem>

        <WiniGridItem size={{ md: 3.4, xs: 12 }}>
          <TangibleAssetEditor
            formData={ctl.editor.formData}
            categoryList={ctl.categoryList}
            locationList={ctl.locationList}
            userList={ctl.userList}
            onChange={ctl.editor.handleChange}
            onReset={ctl.editor.reset}
            onCreate={ctl.editor.handleCreate}
            onUpdate={ctl.editor.handleUpdate}
            isSaving={ctl.editor.isSaving}
            isOptionsLoading={ctl.isOptionsLoading}
            disabled={isOptionsUnavailable || ctl.isListLoading}
          />
          {ctl.editor.formData.tangibleAssetId &&
            !['DISUSE', 'DISPOSED'].includes(
              ctl.editor.formData.lifeStatus,
            ) && (
              <WiniBox className="mt-2 flex justify-end">
                {winiCom.checkMenuAut(
                  'update',
                  <WiniButton
                    ui="lineGray"
                    onClick={() => ctl.disuse.openDialog(ctl.editor.formData)}
                    disabled={ctl.isListLoading || ctl.editor.isSaving}
                  >
                    불용 처리
                  </WiniButton>,
                )}
              </WiniBox>
            )}
        </WiniGridItem>
      </WiniGridLayout>

      {ctl.editor.formData.tangibleAssetId && (
        <WiniGridLayout
          container
          columnSpacing={2}
          className="flex justify-between"
        >
          <WiniGridItem size={{ md: 6, xs: 12 }}>
            <AssignmentHistoryPanel
              history={ctl.assignment.history}
              userList={ctl.userList}
              onRelease={ctl.assignment.release}
              isLoading={ctl.assignment.isLoading}
              isReleasing={ctl.assignment.isReleasing}
            />
          </WiniGridItem>
          <WiniGridItem size={{ md: 6, xs: 12 }}>
            <AssetHistoryPanel
              history={ctl.assetHistory.history}
              isLoading={ctl.assetHistory.isLoading}
            />
          </WiniGridItem>
        </WiniGridLayout>
      )}

      <DuplicateDialog
        open={ctl.bulk.duplicateOpen}
        sourceAsset={ctl.checkedRows[0]}
        duplicateData={ctl.bulk.duplicateData}
        onChange={ctl.bulk.onDuplicateChange}
        onClose={ctl.bulk.closeDuplicate}
        onSubmit={ctl.bulk.submitDuplicate}
        isSubmitting={ctl.bulk.isDuplicating}
      />

      <BatchUpdateDialog
        open={ctl.bulk.batchOpen}
        checkedCount={ctl.checkedRows.length}
        batchData={ctl.bulk.batchData}
        categoryList={ctl.categoryList}
        locationList={ctl.locationList}
        userList={ctl.userList}
        onChange={ctl.bulk.onBatchChange}
        onToggle={ctl.bulk.onBatchToggle}
        onClose={ctl.bulk.closeBatch}
        onSubmit={ctl.bulk.submitBatch}
        isSubmitting={ctl.bulk.isBatchUpdating}
        isOptionsUnavailable={isOptionsUnavailable}
      />

      <ExcelUpsertDialog
        open={ctl.excelUpsert.open}
        file={ctl.excelUpsert.file}
        rows={ctl.excelUpsert.rows}
        isPreviewing={ctl.excelUpsert.isPreviewing}
        isCommitting={ctl.excelUpsert.isCommitting}
        summary={ctl.excelUpsert.summary}
        onFileChange={ctl.excelUpsert.onFileChange}
        onClose={ctl.excelUpsert.closeDialog}
        onDownloadTemplate={ctl.excelUpsert.downloadTemplate}
        onPreview={ctl.excelUpsert.runPreview}
        onCommit={ctl.excelUpsert.runCommit}
      />

      <DisuseDialog
        open={ctl.disuse.open}
        targetAsset={ctl.disuse.targetAsset}
        reason={ctl.disuse.reason}
        isSubmitting={ctl.disuse.isSubmitting}
        onChangeReason={ctl.disuse.onReasonChange}
        onClose={ctl.disuse.closeDialog}
        onSubmit={ctl.disuse.submit}
      />

      <ActivityLogDialog
        open={ctl.activityLog.open}
        filterDraft={ctl.activityLog.filterDraft}
        onChangeFilter={ctl.activityLog.onChangeFilter}
        onChangeDateFilter={ctl.activityLog.onChangeDateFilter}
        onSearch={ctl.activityLog.onSearch}
        log={ctl.activityLog.log}
        isLoading={ctl.activityLog.isLoading}
        hasMore={ctl.activityLog.hasMore}
        loadMore={ctl.activityLog.loadMore}
        onClose={ctl.activityLog.closeDialog}
      />

      <QrLabelPrintArea labels={ctl.qrLabel.labels} />
    </WiniFormNormal>
  );
};

export default TangibleAssetManagementPage;
