package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.Inventory;
import com.winitech.smartAsset.domain.inventory.InventoryExcludedMember;
import com.winitech.smartAsset.domain.inventory.InventoryInspector;
import com.winitech.smartAsset.domain.inventory.InventoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InventoryStoreImpl implements InventoryStore {

    private final InventoryRepository inventoryRepository;
    private final InventoryInspectorRepository inventoryInspectorRepository;
    private final InventoryExcludedMemberRepository inventoryExcludedMemberRepository;

    @Override
    public Inventory store(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public List<InventoryInspector> storeInspectors(List<InventoryInspector> inspectors) {
        return inventoryInspectorRepository.saveAll(inspectors);
    }

    @Override
    public List<InventoryExcludedMember> storeExcludedMembers(List<InventoryExcludedMember> excludedMembers) {
        return inventoryExcludedMemberRepository.saveAll(excludedMembers);
    }
}
