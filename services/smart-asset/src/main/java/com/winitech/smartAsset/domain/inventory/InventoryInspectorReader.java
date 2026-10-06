package com.winitech.smartAsset.domain.inventory;

import java.util.List;
import java.util.UUID;

public interface InventoryInspectorReader {

    List<InventoryInspector> findAllByInventoryId(UUID inventoryId);
}
