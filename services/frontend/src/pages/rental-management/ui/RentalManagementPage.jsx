import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniButton } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';
import { RentalListGrid, RentalListSearch, RentalListPager } from '@/features/rental/list';
import { RentalDialog } from '@/features/rental/manage';
import { RentalDetailDialog } from '@/features/rental/detail';
import { useRentalManagementPage } from '../model/useRentalManagementPage';

/** S-510 렌탈·구독 목록 - S-511(등록/수정)·S-512(상세·결제스케줄)는 이 화면 안에서 모달로 진입한다 */
export const RentalManagementPage = () => {
  const ctl = useRentalManagementPage();
  const { dialog, detail } = ctl;

  return (
    <WiniFormNormal>
      <RentalListSearch
        searchData={ctl.searchData}
        onSearchChange={ctl.onSearchChange}
        onSearch={ctl.onSearch}
        isLoading={ctl.isLoading}
        extraActions={winiCom.checkMenuAut(
          'insert',
          <WiniButton ui="line" onClick={dialog.openCreate}>렌탈·구독 등록</WiniButton>,
        )}
      />

      <RentalListGrid rowData={ctl.list} isLoading={ctl.isLoading} onRowSelect={ctl.onRowSelect} />
      <RentalListPager pageInfo={ctl.pageInfo} onPageChange={ctl.onPageChange} isLoading={ctl.isLoading} />

      <RentalDialog
        open={dialog.open}
        form={dialog.form}
        isSubmitting={dialog.isSubmitting}
        onClose={dialog.closeDialog}
        onChange={dialog.handleChange}
        onDateChange={dialog.handleDateChange}
        onSubmit={dialog.submit}
      />

      <RentalDetailDialog
        row={detail.selectedRow}
        schedules={detail.schedules}
        isLoading={detail.isLoading}
        isActing={detail.isActing}
        newSchedule={detail.newSchedule}
        actualAmountDraft={detail.actualAmountDraft}
        onNewScheduleChange={detail.handleNewScheduleChange}
        onNewScheduleDateChange={detail.handleNewScheduleDateChange}
        onAddSchedule={detail.addSchedule}
        onActualAmountChange={detail.setActualAmountForRow}
        onConfirmSchedule={detail.confirmSchedule}
        onEdit={ctl.onEditFromDetail}
        onCancel={detail.cancel}
        onClose={detail.closeDetail}
      />
    </WiniFormNormal>
  );
};

export default RentalManagementPage;
