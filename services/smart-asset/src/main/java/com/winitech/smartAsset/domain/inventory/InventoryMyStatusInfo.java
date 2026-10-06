package com.winitech.smartAsset.domain.inventory;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * S-310 "내 전수조사" - 로그인한 임직원 본인이 배정받은, 진행 중인 조사의 대상 목록.
 * I2(동일 임직원은 하나의 진행 중 조사에만 참여)에 의해 대상 조사는 항상 최대 1건이다.
 */
@Getter
public class InventoryMyStatusInfo {

    private final boolean hasActiveInventory;
    private final UUID inventoryId;
    private final String title;
    private final Boolean approvalRequired;
    private final List<InventoryResultRowInfo> results;

    public static InventoryMyStatusInfo none() {
        return new InventoryMyStatusInfo(false, null, null, null, List.of());
    }

    public InventoryMyStatusInfo(boolean hasActiveInventory, UUID inventoryId, String title,
                                  Boolean approvalRequired, List<InventoryResultRowInfo> results) {
        this.hasActiveInventory = hasActiveInventory;
        this.inventoryId = inventoryId;
        this.title = title;
        this.approvalRequired = approvalRequired;
        this.results = results;
    }
}
