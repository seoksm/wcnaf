package com.winitech.smartAsset.domain.inventory;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * S-306 반복 시행 스케줄 - 유형(임직원형/관리자형)별로 "가장 최근 종료된 조사 + 그 조사의 반복 주기"를
 * 기준으로 다음 시행 예정일을 계산해 보여준다. §4 "자동 시행은 직전 조사 종료를 전제로 한다(I2)"의
 * 자동 실행(cron)까지는 이번 단계에서 만들지 않고(알림 인프라가 없어 "실행 건너뜀" 안내를 보낼 방법이
 * 없다), 예정일 안내 + 직전 조사 설정 그대로 복제해 새로 만드는 수동 동선까지만 지원한다.
 */
@Getter
public class InventoryScheduleInfo {

    private final Inventory.InventoryType inventoryType;
    private final UUID lastClosedInventoryId;
    private final String lastClosedTitle;
    private final OffsetDateTime lastClosedAt;
    private final Inventory.RecurrenceRule recurrenceRule;
    private final OffsetDateTime nextDueDate;
    private final boolean overdue;
    /** 이미 다음 회차가 진행 중이면 예정일 안내 자체가 의미 없다(I2) */
    private final boolean nextCycleInProgress;

    public InventoryScheduleInfo(Inventory lastClosedInventory, OffsetDateTime nextDueDate,
                                  boolean nextCycleInProgress) {
        this.inventoryType = lastClosedInventory.getInventoryType();
        this.lastClosedInventoryId = lastClosedInventory.getId();
        this.lastClosedTitle = lastClosedInventory.getTitle();
        this.lastClosedAt = lastClosedInventory.getClosedAt();
        this.recurrenceRule = lastClosedInventory.getRecurrenceRule();
        this.nextDueDate = nextDueDate;
        this.overdue = !nextCycleInProgress && nextDueDate != null && nextDueDate.isBefore(OffsetDateTime.now());
        this.nextCycleInProgress = nextCycleInProgress;
    }
}
