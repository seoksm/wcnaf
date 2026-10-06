package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.InventoryInspector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryInspectorRepository extends JpaRepository<InventoryInspector, UUID> {

    List<InventoryInspector> findAllByInventory_Id(UUID inventoryId);
}
