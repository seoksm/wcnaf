package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.InventoryInspector;
import com.winitech.smartAsset.domain.inventory.InventoryInspectorReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryInspectorReaderImpl implements InventoryInspectorReader {

    private final InventoryInspectorRepository inventoryInspectorRepository;

    @Override
    public List<InventoryInspector> findAllByInventoryId(UUID inventoryId) {
        return inventoryInspectorRepository.findAllByInventory_Id(inventoryId);
    }
}
