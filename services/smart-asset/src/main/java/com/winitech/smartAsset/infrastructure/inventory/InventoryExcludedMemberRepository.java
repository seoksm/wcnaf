package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.InventoryExcludedMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryExcludedMemberRepository extends JpaRepository<InventoryExcludedMember, UUID> {

    List<InventoryExcludedMember> findAllByInventory_Id(UUID inventoryId);
}
