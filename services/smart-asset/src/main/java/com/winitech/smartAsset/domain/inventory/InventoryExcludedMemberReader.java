package com.winitech.smartAsset.domain.inventory;

import java.util.List;
import java.util.UUID;

public interface InventoryExcludedMemberReader {

    List<InventoryExcludedMember> findAllByInventoryId(UUID inventoryId);
}
