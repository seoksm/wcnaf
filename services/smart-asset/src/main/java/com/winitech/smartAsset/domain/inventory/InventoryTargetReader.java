package com.winitech.smartAsset.domain.inventory;

import java.util.List;
import java.util.UUID;

public interface InventoryTargetReader {

    InventoryTarget findById(UUID inventoryTargetId);

    List<InventoryTarget> findAllByInventoryId(UUID inventoryId);

    long countByInventoryId(UUID inventoryId);

    /** S-306 이월 추적 - 직전 조사에서 이 자산이 차기이월(CARRY_OVER)로 종결됐는지 확인 */
    boolean existsCarriedOverInPreviousInventory(UUID previousInventoryId, UUID tangibleAssetId);
}
