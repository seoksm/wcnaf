package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.InventoryExcludedMember;
import com.winitech.smartAsset.domain.inventory.InventoryExcludedMemberReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryExcludedMemberReaderImpl implements InventoryExcludedMemberReader {

    private final InventoryExcludedMemberRepository inventoryExcludedMemberRepository;

    @Override
    public List<InventoryExcludedMember> findAllByInventoryId(UUID inventoryId) {
        return inventoryExcludedMemberRepository.findAllByInventory_Id(inventoryId);
    }
}
