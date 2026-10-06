package com.winitech.smartAsset.domain.ticket;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.acknowledgement.Acknowledgement;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementService;
import com.winitech.smartAsset.domain.license.LicenseAssignedUser;
import com.winitech.smartAsset.domain.license.LicenseAssignedUserReader;
import com.winitech.smartAsset.domain.license.LicenseService;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketServiceImpl extends EgovAbstractServiceImpl implements TicketService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createAt");
    /** 설계문서 "완료 컬럼은 이번 주 것만" - 7일로 단순화 */
    private static final int KANBAN_DONE_WINDOW_DAYS = 7;

    private final TicketReader ticketReader;
    private final TicketStore ticketStore;
    private final TicketCommentReader ticketCommentReader;
    private final TicketCommentStore ticketCommentStore;
    private final TicketTypeConfigReader ticketTypeConfigReader;
    private final TangibleAssetReader tangibleAssetReader;
    private final TangibleAssetService tangibleAssetService;
    private final LicenseAssignedUserReader licenseAssignedUserReader;
    private final LicenseService licenseService;
    private final AcknowledgementService acknowledgementService;
    private final LoginUserContext loginUserContext;

    @Transactional
    @Override
    public UUID createTicketBySelf(TicketCommand command) {
        return create(command, loginUserContext.getUserId());
    }

    @Transactional
    @Override
    public UUID createTicketByAdmin(TicketCommand command, UUID requestedBy) {
        return create(command, requestedBy);
    }

    private UUID create(TicketCommand command, UUID requestedBy) {
        TangibleAsset tangibleAsset = command.getTangibleAssetId() != null
                ? tangibleAssetReader.findById(command.getTangibleAssetId()) : null;
        LocalDate targetDueDate = calculateTargetDueDate(command.getTicketType());

        Ticket ticket = Ticket.builder()
                .ticketType(command.getTicketType())
                .title(command.getTitle())
                .content(command.getContent())
                .requestedBy(requestedBy)
                .tangibleAsset(tangibleAsset)
                .targetDueDate(targetDueDate)
                .build();
        return ticketStore.store(ticket).getId();
    }

    @Transactional
    @Override
    public UUID createReturnTicket(UUID licenseAssignedUserId, UUID requestedBy, String licenseName) {
        LicenseAssignedUser assignedUser = licenseAssignedUserReader.findById(licenseAssignedUserId);
        LocalDate targetDueDate = calculateTargetDueDate(Ticket.Type.RETURN);

        Ticket ticket = Ticket.builder()
                .ticketType(Ticket.Type.RETURN)
                .title("라이선스 회수 - " + licenseName)
                .content("임직원이 내 라이선스 화면에서 회수를 요청했습니다.")
                .requestedBy(requestedBy)
                .licenseAssignedUser(assignedUser)
                .targetDueDate(targetDueDate)
                .build();
        return ticketStore.store(ticket).getId();
    }

    private LocalDate calculateTargetDueDate(Ticket.Type ticketType) {
        TicketTypeConfig config = ticketTypeConfigReader.findByTicketType(ticketType);
        return config.getTargetDays() != null ? LocalDate.now().plusDays(config.getTargetDays()) : null;
    }

    @Transactional
    @Override
    public void updateTicket(TicketCommand.UpdateCommand updateCommand) {
        Ticket ticket = ticketReader.findById(updateCommand.getTicketId());
        ticketStore.modify(ticket, updateCommand.getTitle(), updateCommand.getContent());
    }

    @Transactional
    @Override
    public void assignTicket(UUID ticketId, UUID assigneeId) {
        Ticket ticket = ticketReader.findById(ticketId);
        ticketStore.assign(ticket, assigneeId);
    }

    @Transactional
    @Override
    public void changeStatus(UUID ticketId, Ticket.Status newStatus) {
        Ticket ticket = ticketReader.findById(ticketId);
        ticketStore.moveStatus(ticket, newStatus);
    }

    @Transactional
    @Override
    public void completeTicket(UUID ticketId, TicketCompleteCommand completeCommand) {
        Ticket ticket = ticketReader.findById(ticketId);

        if (ticket.getTicketType() == Ticket.Type.PURCHASE) {
            completeAsPurchase(ticket, completeCommand);
            return;
        }
        if (ticket.getTicketType() == Ticket.Type.RETURN) {
            completeAsReturn(ticket);
            return;
        }
        ticketStore.complete(ticket, null);
    }

    /** Q-49 연쇄 처리 - 자산 생성(요청자 개인배정 포함) → 수령확인서 요청 → 티켓 완료·자산 연결 */
    private void completeAsPurchase(Ticket ticket, TicketCompleteCommand cmd) {
        if (cmd == null || cmd.getAssetName() == null || cmd.getCategoryId() == null
                || cmd.getLocationId() == null || cmd.getAcquisitionDate() == null || cmd.getAcquisitionAmount() == null) {
            throw new InvalidParamException("신규구매 완료 처리에는 자산명·종류·위치·취득일·취득가액이 모두 필요합니다.");
        }

        TangibleAssetCommand assetCommand = TangibleAssetCommand.builder()
                .assetName(cmd.getAssetName())
                .categoryId(cmd.getCategoryId())
                .locationId(cmd.getLocationId())
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.PERSONAL)
                .acquisitionDate(cmd.getAcquisitionDate())
                .acquisitionAmount(cmd.getAcquisitionAmount())
                .modelName(cmd.getModelName())
                .manufacturer(cmd.getManufacturer())
                .serialNo(cmd.getSerialNo())
                .currentMemberId(ticket.getRequestedBy())
                .memo(cmd.getMemo())
                .build();
        UUID createdAssetId = tangibleAssetService.createTangibleAsset(assetCommand);
        TangibleAsset createdAsset = tangibleAssetReader.findById(createdAssetId);

        acknowledgementService.requestAcknowledgement(
                createdAssetId, ticket.getRequestedBy(), cmd.getRequesterName(), Acknowledgement.Type.RECEIPT, cmd.getManagerName());

        ticketStore.complete(ticket, createdAsset);
    }

    /** 라이선스 배정 회수 - 잔여 수량이 늘어난다 */
    private void completeAsReturn(Ticket ticket) {
        if (ticket.getLicenseAssignedUser() != null) {
            licenseService.releaseUser(ticket.getLicenseAssignedUser().getId());
        }
        ticketStore.complete(ticket, null);
    }

    @Override
    public List<TicketInfo> loadKanbanBoard() {
        OffsetDateTime doneSince = OffsetDateTime.now().minusDays(KANBAN_DONE_WINDOW_DAYS);
        return ticketReader.findKanbanBoard(doneSince).stream().map(TicketInfo::new).collect(Collectors.toList());
    }

    @Override
    public Page<TicketInfo> loadList(String keyword, Ticket.Status status, Integer page, Integer size) {
        return ticketReader.findAll(keyword, status, toPageable(page, size)).map(TicketInfo::new);
    }

    @Override
    public TicketInfo loadTicket(UUID ticketId) {
        return new TicketInfo(ticketReader.findById(ticketId));
    }

    @Override
    public List<TicketInfo> loadMyTickets() {
        return ticketReader.findAllByRequestedBy(loginUserContext.getUserId()).stream()
                .map(TicketInfo::new).collect(Collectors.toList());
    }

    @Override
    public List<TicketCommentInfo> loadComments(UUID ticketId) {
        return ticketCommentReader.findAllByTicketId(ticketId).stream()
                .map(TicketCommentInfo::new).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UUID addComment(UUID ticketId, String content) {
        Ticket ticket = ticketReader.findById(ticketId);
        UUID writtenBy = loginUserContext.getUserId();
        boolean isRequester = ticket.getRequestedBy().equals(writtenBy);

        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .content(content)
                .writtenBy(writtenBy)
                .isRequester(isRequester)
                .build();
        return ticketCommentStore.store(comment).getId();
    }

    private Pageable toPageable(Integer page, Integer size) {
        int safePage = (page == null || page < 0) ? 0 : page;
        int safeSize = (size == null) ? DEFAULT_PAGE_SIZE : Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        return PageRequest.of(safePage, safeSize, DEFAULT_SORT);
    }
}
