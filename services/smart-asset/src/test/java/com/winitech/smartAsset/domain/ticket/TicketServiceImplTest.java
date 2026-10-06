package com.winitech.smartAsset.domain.ticket;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.smartAsset.domain.acknowledgement.Acknowledgement;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementService;
import com.winitech.smartAsset.domain.license.LicenseAssignedUser;
import com.winitech.smartAsset.domain.license.LicenseAssignedUserReader;
import com.winitech.smartAsset.domain.license.LicenseService;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * S-602 완료 처리의 유형별 연쇄 처리(Q-49 PURCHASE 자산 자동등록, RETURN 라이선스 배정 회수)를
 * 검증한다. Reader/Store와 다른 도메인 서비스는 모두 모킹하는 순수 Mockito 단위 테스트다.
 */
@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock private TicketReader ticketReader;
    @Mock private TicketStore ticketStore;
    @Mock private TicketCommentReader ticketCommentReader;
    @Mock private TicketCommentStore ticketCommentStore;
    @Mock private TicketTypeConfigReader ticketTypeConfigReader;
    @Mock private TangibleAssetReader tangibleAssetReader;
    @Mock private TangibleAssetService tangibleAssetService;
    @Mock private LicenseAssignedUserReader licenseAssignedUserReader;
    @Mock private LicenseService licenseService;
    @Mock private AcknowledgementService acknowledgementService;
    @Mock private LoginUserContext loginUserContext;

    private TicketServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TicketServiceImpl(
                ticketReader, ticketStore, ticketCommentReader, ticketCommentStore, ticketTypeConfigReader,
                tangibleAssetReader, tangibleAssetService, licenseAssignedUserReader, licenseService,
                acknowledgementService, loginUserContext);
    }

    @Test
    void createTicketBySelf는_로그인_사용자를_요청자로_기록한다() {
        UUID myId = UUID.randomUUID();
        when(loginUserContext.getUserId()).thenReturn(myId);
        TicketTypeConfig config = mockConfig(3);
        when(ticketTypeConfigReader.findByTicketType(Ticket.Type.REPAIR)).thenReturn(config);
        when(ticketStore.store(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketCommand command = TicketCommand.builder().ticketType(Ticket.Type.REPAIR).title("t").build();
        service.createTicketBySelf(command);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketStore).store(captor.capture());
        assertThat(captor.getValue().getRequestedBy()).isEqualTo(myId);
        assertThat(captor.getValue().getTargetDueDate()).isEqualTo(LocalDate.now().plusDays(3));
    }

    @Test
    void PURCHASE_완료_처리는_자산을_생성하고_수령확인서를_요청한다() {
        UUID requesterId = UUID.randomUUID();
        UUID createdAssetId = UUID.randomUUID();
        Ticket ticket = Ticket.builder()
                .ticketType(Ticket.Type.PURCHASE)
                .title("노트북 신규구매")
                .requestedBy(requesterId)
                .build();
        when(ticketReader.findById(any())).thenReturn(ticket);
        when(tangibleAssetService.createTangibleAsset(any(TangibleAssetCommand.class))).thenReturn(createdAssetId);
        TangibleAsset createdAsset = org.mockito.Mockito.mock(TangibleAsset.class);
        when(tangibleAssetReader.findById(createdAssetId)).thenReturn(createdAsset);

        TicketCompleteCommand completeCommand = TicketCompleteCommand.builder()
                .assetName("맥북프로")
                .categoryId(UUID.randomUUID())
                .locationId(UUID.randomUUID())
                .acquisitionDate(LocalDate.now())
                .acquisitionAmount(new BigDecimal("2000000"))
                .requesterName("홍길동")
                .managerName("김관리")
                .build();

        service.completeTicket(UUID.randomUUID(), completeCommand);

        ArgumentCaptor<TangibleAssetCommand> assetCommandCaptor = ArgumentCaptor.forClass(TangibleAssetCommand.class);
        verify(tangibleAssetService).createTangibleAsset(assetCommandCaptor.capture());
        assertThat(assetCommandCaptor.getValue().getCurrentMemberId()).isEqualTo(requesterId);
        assertThat(assetCommandCaptor.getValue().getAssignType()).isEqualTo(TangibleAsset.AssignType.PERSONAL);

        verify(acknowledgementService).requestAcknowledgement(
                createdAssetId, requesterId, "홍길동", Acknowledgement.Type.RECEIPT, "김관리");
        verify(ticketStore).complete(ticket, createdAsset);
    }

    @Test
    void RETURN_완료_처리는_라이선스_배정을_회수한다() {
        UUID licenseAssignedUserId = UUID.randomUUID();
        LicenseAssignedUser assignedUser = org.mockito.Mockito.mock(LicenseAssignedUser.class);
        when(assignedUser.getId()).thenReturn(licenseAssignedUserId);

        Ticket ticket = Ticket.builder()
                .ticketType(Ticket.Type.RETURN)
                .title("라이선스 회수")
                .requestedBy(UUID.randomUUID())
                .licenseAssignedUser(assignedUser)
                .build();
        when(ticketReader.findById(any())).thenReturn(ticket);

        service.completeTicket(UUID.randomUUID(), null);

        verify(licenseService).releaseUser(licenseAssignedUserId);
        verify(ticketStore).complete(ticket, null);
    }

    private TicketTypeConfig mockConfig(Integer targetDays) {
        TicketTypeConfig config = org.mockito.Mockito.mock(TicketTypeConfig.class);
        when(config.getTargetDays()).thenReturn(targetDays);
        return config;
    }
}
