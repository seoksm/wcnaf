package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.InventoryTarget;
import com.winitech.smartAsset.domain.inventory.InventoryTargetStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InventoryTargetStoreImpl implements InventoryTargetStore {

    private final InventoryTargetRepository inventoryTargetRepository;

    @Override
    public List<InventoryTarget> storeAll(List<InventoryTarget> targets) {
        return inventoryTargetRepository.saveAll(targets);
    }
}
