package com.winitech.smartAsset.domain.inventory;

import java.util.List;

public interface InventoryStore {

    Inventory store(Inventory inventory);

    List<InventoryInspector> storeInspectors(List<InventoryInspector> inspectors);

    List<InventoryExcludedMember> storeExcludedMembers(List<InventoryExcludedMember> excludedMembers);
}
