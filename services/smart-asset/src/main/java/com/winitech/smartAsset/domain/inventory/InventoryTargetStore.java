package com.winitech.smartAsset.domain.inventory;

import java.util.List;

public interface InventoryTargetStore {

    List<InventoryTarget> storeAll(List<InventoryTarget> targets);
}
