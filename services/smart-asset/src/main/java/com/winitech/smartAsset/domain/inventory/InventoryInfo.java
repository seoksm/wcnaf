package com.winitech.smartAsset.domain.inventory;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/** S-300 목록 · S-301/302 생성 완료 응답에 쓰는 조사 1건 요약 */
@Getter
public class InventoryInfo {

    private final UUID inventoryId;
    private final String title;
    private final Inventory.InventoryType inventoryType;
    private final Inventory.Status status;
    private final Boolean approvalRequired;
    private final Boolean allowNewAssetRegistration;
    private final Inventory.RecurrenceRule recurrenceRule;
    private final OffsetDateTime createAt;
    private final OffsetDateTime closedAt;
    /** inventory 엔티티 자체에는 없는 값 - 서비스가 InventoryTargetReader로 따로 집계해 채운다 */
    private final long targetCount;

    public InventoryInfo(Inventory inventory, long targetCount) {
        this.inventoryId = inventory.getId();
        this.title = inventory.getTitle();
        this.inventoryType = inventory.getInventoryType();
        this.status = inventory.getStatus();
        this.approvalRequired = inventory.getApprovalRequired();
        this.allowNewAssetRegistration = inventory.getAllowNewAssetRegistration();
        this.recurrenceRule = inventory.getRecurrenceRule();
        this.createAt = inventory.getCreateAt();
        this.closedAt = inventory.getClosedAt();
        this.targetCount = targetCount;
    }
}
