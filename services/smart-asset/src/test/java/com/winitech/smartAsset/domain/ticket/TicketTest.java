package com.winitech.smartAsset.domain.ticket;

import com.winitech.common.exception.InvalidParamException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 설계문서 §4 "완료 잠금" - DONE으로 전환된 티켓은 어떤 방식으로도 다시 바뀌지 않아야 한다.
 * 순수 엔티티 단위 테스트(Spring 컨텍스트 불필요).
 */
class TicketTest {

    private Ticket newTicket() {
        return Ticket.builder()
                .ticketType(Ticket.Type.REPAIR)
                .title("모니터 화면 깜빡임")
                .content("전원 켤 때마다 깜빡거립니다")
                .requestedBy(UUID.randomUUID())
                .build();
    }

    @Test
    void 생성_직후_상태는_WAITING이다() {
        Ticket ticket = newTicket();
        assertThat(ticket.getStatus()).isEqualTo(Ticket.Status.WAITING);
    }

    @Test
    void moveStatus로는_DONE으로_갈_수_없다() {
        Ticket ticket = newTicket();
        assertThatThrownBy(() -> ticket.moveStatus(Ticket.Status.DONE))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void complete_이후에는_modify_assign_moveStatus_complete_모두_거부된다() {
        Ticket ticket = newTicket();
        ticket.complete(null);

        assertThat(ticket.getStatus()).isEqualTo(Ticket.Status.DONE);
        assertThatThrownBy(() -> ticket.modify("새 제목", "새 내용")).isInstanceOf(InvalidParamException.class);
        assertThatThrownBy(() -> ticket.assign(UUID.randomUUID())).isInstanceOf(InvalidParamException.class);
        assertThatThrownBy(() -> ticket.moveStatus(Ticket.Status.IN_PROGRESS)).isInstanceOf(InvalidParamException.class);
        assertThatThrownBy(() -> ticket.complete(null)).isInstanceOf(InvalidParamException.class);
    }

    @Test
    void targetDueDate가_지났고_아직_완료가_아니면_overdue다() {
        Ticket ticket = Ticket.builder()
                .ticketType(Ticket.Type.REPAIR)
                .title("t")
                .requestedBy(UUID.randomUUID())
                .targetDueDate(java.time.LocalDate.now().minusDays(1))
                .build();

        assertThat(ticket.isOverdue()).isTrue();

        ticket.complete(null);
        assertThat(ticket.isOverdue()).as("완료되면 기한을 넘겨도 overdue가 아니다").isFalse();
    }
}
