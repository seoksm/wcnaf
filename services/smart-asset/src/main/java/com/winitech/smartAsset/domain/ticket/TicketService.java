package com.winitech.smartAsset.domain.ticket;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    /** S-610 임직원 본인 등록 - requestedBy는 LoginUserContext에서 채운다 */
    UUID createTicketBySelf(TicketCommand command);

    /** S-603 관리자 등록 - requestedBy를 관리자가 직접 지정한다 */
    UUID createTicketByAdmin(TicketCommand command, UUID requestedBy);

    /** 라이선스 회수 요청(S-550)에서 RETURN 티켓을 자동 생성한다 */
    UUID createReturnTicket(UUID licenseAssignedUserId, UUID requestedBy, String licenseName);

    void updateTicket(TicketCommand.UpdateCommand updateCommand);

    void assignTicket(UUID ticketId, UUID assigneeId);

    /** DONE으로는 이 메서드로 이동할 수 없다 - completeTicket()을 써야 한다 */
    void changeStatus(UUID ticketId, Ticket.Status newStatus);

    /**
     * S-602 완료 처리. PURCHASE 유형이면 completeCommand로 자산을 등록하고(Q-49) 요청자에게
     * 배정한 뒤 수령확인서를 요청한다(연쇄 처리). RETURN 유형이면 연결된 라이선스 배정을
     * 회수한다. 그 외 유형은 상태만 완료로 바뀐다.
     */
    void completeTicket(UUID ticketId, TicketCompleteCommand completeCommand);

    /** S-600 칸반 - WAITING/RECEIVED/IN_PROGRESS 전건 + DONE은 최근 7일분만 */
    List<TicketInfo> loadKanbanBoard();

    /** S-601 목록 */
    Page<TicketInfo> loadList(String keyword, Ticket.Status status, Integer page, Integer size);

    TicketInfo loadTicket(UUID ticketId);

    /** S-611 내 티켓 */
    List<TicketInfo> loadMyTickets();

    List<TicketCommentInfo> loadComments(UUID ticketId);

    /** 관리자·임직원 공용 - writtenBy는 LoginUserContext, isRequester는 ticket.requestedBy와 비교해 서버가 계산 */
    UUID addComment(UUID ticketId, String content);
}
