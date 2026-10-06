import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';
import { TicketListGrid, TicketListSearch, TicketListPager } from '@/features/ticket/list';
import { KanbanBoard } from '@/features/ticket/kanban';
import { TicketDialog } from '@/features/ticket/manage';
import { TicketDetailDialog } from '@/features/ticket/detail';
import { useTicketManagementPage } from '../model/useTicketManagementPage';

/**
 * S-600 칸반 보드(데스크톱) · S-601 목록(<768 자동 전환) - 하나의 페이지에서 반응형으로
 * 처리한다(설계문서 C4). S-602(상세·코멘트)·S-603(등록)은 이 화면 안에서 모달로 진입한다.
 */
export const TicketManagementPage = () => {
  const ctl = useTicketManagementPage();
  const { dialog, detail } = ctl;

  return (
    <WiniFormNormal>
      <WiniBox className="mb-2 flex justify-end">
        {winiCom.checkMenuAut(
          'insert',
          <WiniButton ui="line" onClick={dialog.openCreate}>티켓 등록</WiniButton>,
        )}
      </WiniBox>

      {ctl.isMobile ? (
        <>
          <TicketListSearch
            searchData={ctl.searchData}
            onSearchChange={ctl.onSearchChange}
            onSearch={ctl.onSearch}
            isLoading={ctl.list.isLoading}
          />
          <TicketListGrid rowData={ctl.list.list} isLoading={ctl.list.isLoading} onRowSelect={ctl.onCardClick} />
          <TicketListPager pageInfo={ctl.list.pageInfo} onPageChange={ctl.onPageChange} />
        </>
      ) : (
        <>
          {ctl.kanban.isLoading ? (
            <WiniTypography variant="span" className="text-xs text-text-sub">불러오는 중...</WiniTypography>
          ) : (
            <KanbanBoard
              columns={ctl.kanban.columns}
              isActing={ctl.kanban.isActing}
              onMoveTicket={ctl.kanban.moveTicket}
              onCardClick={ctl.onCardClick}
            />
          )}
        </>
      )}

      <TicketDialog
        open={dialog.open}
        form={dialog.form}
        userList={dialog.userList}
        isResolving={dialog.isResolving}
        isSubmitting={dialog.isSubmitting}
        onClose={dialog.closeDialog}
        onChange={dialog.handleChange}
        onResolveAsset={dialog.resolveAsset}
        onSubmit={dialog.submit}
      />

      <TicketDetailDialog
        ticket={detail.selectedTicket}
        comments={detail.comments}
        userList={detail.userList}
        categoryList={detail.categoryList}
        locationList={detail.locationList}
        isLoading={detail.isLoading}
        isActing={detail.isActing}
        newComment={detail.newComment}
        assigneeDraft={detail.assigneeDraft}
        completeForm={detail.completeForm}
        userNameById={detail.userNameById}
        onAssigneeChange={detail.handleAssigneeChange}
        onSubmitAssign={detail.submitAssign}
        onChangeStatus={detail.changeStatus}
        onCompleteFormChange={detail.handleCompleteFormChange}
        onSubmitComplete={detail.submitComplete}
        onNewCommentChange={detail.setNewComment}
        onSubmitComment={detail.submitComment}
        onClose={detail.closeDetail}
      />
    </WiniFormNormal>
  );
};

export default TicketManagementPage;
