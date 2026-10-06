package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.Inventory;
import com.winitech.smartAsset.domain.inventory.InventoryResult;
import com.winitech.smartAsset.domain.inventory.InventoryResultReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryResultReaderImpl implements InventoryResultReader {

    private final InventoryResultRepository inventoryResultRepository;

    @Override
    public InventoryResult findById(UUID inventoryResultId) {
        return inventoryResultRepository.findById(inventoryResultId).orElseThrow();
    }

    @Override
    public InventoryResult findByInventoryTargetId(UUID inventoryTargetId) {
        return inventoryResultRepository.findByInventoryTarget_Id(inventoryTargetId).orElseThrow();
    }

    @Override
    public List<InventoryResult> findAllByInventoryId(UUID inventoryId) {
        return inventoryResultRepository.findAllByInventoryTarget_Inventory_Id(inventoryId);
    }

    @Override
    public List<InventoryResult> findAllByInventoryIdAndStatus(UUID inventoryId, InventoryResult.Status status) {
        return inventoryResultRepository.findAllByInventoryTarget_Inventory_IdAndStatus(inventoryId, status);
    }

    @Override
    public List<InventoryResult> findAllByMemberIdAndInventoryStatus(UUID memberId, Inventory.Status status) {
        return inventoryResultRepository.findAllByInventoryTarget_MemberIdAndInventoryTarget_Inventory_Status(memberId, status);
    }
}
