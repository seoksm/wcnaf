package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.InventoryResult;
import com.winitech.smartAsset.domain.inventory.InventoryResultStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InventoryResultStoreImpl implements InventoryResultStore {

    private final InventoryResultRepository inventoryResultRepository;

    @Override
    public InventoryResult store(InventoryResult inventoryResult) {
        return inventoryResultRepository.save(inventoryResult);
    }

    @Override
    public List<InventoryResult> storeAll(List<InventoryResult> inventoryResults) {
        return inventoryResultRepository.saveAll(inventoryResults);
    }
}
