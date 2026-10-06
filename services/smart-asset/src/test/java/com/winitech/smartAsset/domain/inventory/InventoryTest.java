package com.winitech.smartAsset.domain.inventory;

import com.winitech.common.exception.InvalidParamException;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryTest {

    private Inventory newInventory() {
        return Inventory.builder()
                .title("2026년 하반기 전수조사")
                .inventoryType(Inventory.InventoryType.MEMBER)
                .createdBy(UUID.randomUUID())
                .build();
    }

    @Test
    void approvalRequired와_allowNewAssetRegistration은_null이면_false로_정규화된다() {
        Inventory inventory = Inventory.builder()
                .title("테스트")
                .inventoryType(Inventory.InventoryType.ADMIN)
                .createdBy(UUID.randomUUID())
                .build();

        assertThat(inventory.getApprovalRequired()).isFalse();
        assertThat(inventory.getAllowNewAssetRegistration()).isFalse();
        assertThat(inventory.getStatus()).isEqualTo(Inventory.Status.IN_PROGRESS);
    }

    @Test
    void close_하면_종료상태가_되고_종료시각_종료자가_기록된다() {
        Inventory inventory = newInventory();
        UUID closedBy = UUID.randomUUID();

        inventory.close(closedBy);

        assertThat(inventory.getStatus()).isEqualTo(Inventory.Status.CLOSED);
        assertThat(inventory.getClosedBy()).isEqualTo(closedBy);
        assertThat(inventory.getClosedAt()).isNotNull();
    }

    @Test
    void 이미_종료된_조사는_다시_종료할_수_없다() {
        Inventory inventory = newInventory();
        inventory.close(UUID.randomUUID());

        assertThatThrownBy(() -> inventory.close(UUID.randomUUID()))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void assertInProgress는_종료된_조사에서_예외를_던진다() {
        Inventory inventory = newInventory();
        inventory.close(UUID.randomUUID());

        assertThatThrownBy(inventory::assertInProgress).isInstanceOf(InvalidParamException.class);
    }

    @Test
    void assertInProgress는_진행중인_조사에서는_통과한다() {
        Inventory inventory = newInventory();

        assertThatCode(inventory::assertInProgress).doesNotThrowAnyException();
    }

    @Test
    void RecurrenceRule의_nextDueDate는_종료시점부터_주기만큼_더한다() {
        OffsetDateTime closedAt = OffsetDateTime.parse("2026-01-15T09:00:00+09:00");

        assertThat(Inventory.RecurrenceRule.QUARTERLY.nextDueDate(closedAt)).isEqualTo(closedAt.plusMonths(3));
        assertThat(Inventory.RecurrenceRule.SEMIANNUAL.nextDueDate(closedAt)).isEqualTo(closedAt.plusMonths(6));
        assertThat(Inventory.RecurrenceRule.ANNUAL.nextDueDate(closedAt)).isEqualTo(closedAt.plusMonths(12));
    }
}
