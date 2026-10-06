package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.InventoryTarget;
import com.winitech.smartAsset.domain.inventory.InventoryTargetReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryTargetReaderImpl implements InventoryTargetReader {

    private final InventoryTargetRepository inventoryTargetRepository;

    @Override
    public InventoryTarget findById(UUID inventoryTargetId) {
        return inventoryTargetRepository.findById(inventoryTargetId).orElseThrow();
    }

    @Override
    public List<InventoryTarget> findAllByInventoryId(UUID inventoryId) {
        return inventoryTargetRepository.findAllByInventory_Id(inventoryId);
    }

    @Override
    public long countByInventoryId(UUID inventoryId) {
        return inventoryTargetRepository.countByInventory_Id(inventoryId);
    }

    @Override
    public boolean existsCarriedOverInPreviousInventory(UUID previousInventoryId, UUID tangibleAssetId) {
        return inventoryTargetRepository.existsCarriedOver(
                previousInventoryId, tangibleAssetId, com.winitech.smartAsset.domain.inventory.InventoryResult.ClosureAction.CARRY_OVER);
    }
}
