package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.InventoryResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryResultRepository extends JpaRepository<InventoryResult, UUID> {

    @EntityGraph(attributePaths = {"inventoryTarget", "inventoryTarget.tangibleAsset", "inventoryTarget.tangibleAsset.category"})
    Optional<InventoryResult> findByInventoryTarget_Id(UUID inventoryTargetId);

    @EntityGraph(attributePaths = {"inventoryTarget", "inventoryTarget.tangibleAsset", "inventoryTarget.tangibleAsset.category"})
    List<InventoryResult> findAllByInventoryTarget_Inventory_Id(UUID inventoryId);

    List<InventoryResult> findAllByInventoryTarget_Inventory_IdAndStatus(UUID inventoryId, InventoryResult.Status status);

    /** S-310 "내 전수조사" - 본인이 배정받은 대상 중, 진행 중인 조사에 속한 것만(I2: 항상 최대 1건) */
    @EntityGraph(attributePaths = {"inventoryTarget", "inventoryTarget.tangibleAsset", "inventoryTarget.tangibleAsset.category", "inventoryTarget.inventory"})
    List<InventoryResult> findAllByInventoryTarget_MemberIdAndInventoryTarget_Inventory_Status(
            UUID memberId, com.winitech.smartAsset.domain.inventory.Inventory.Status status);
}
