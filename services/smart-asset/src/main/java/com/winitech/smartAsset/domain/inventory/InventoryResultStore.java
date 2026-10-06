package com.winitech.smartAsset.domain.inventory;

import java.util.List;

public interface InventoryResultStore {

    InventoryResult store(InventoryResult inventoryResult);

    List<InventoryResult> storeAll(List<InventoryResult> inventoryResults);
}
