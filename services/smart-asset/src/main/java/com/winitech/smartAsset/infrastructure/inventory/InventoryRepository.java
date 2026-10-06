package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findFirstByInventoryTypeAndStatusOrderByClosedAtDesc(
            Inventory.InventoryType inventoryType, Inventory.Status status);

    boolean existsByInventoryTypeAndStatus(Inventory.InventoryType inventoryType, Inventory.Status status);

    /** S-700 전수조사 진행률 - 진행 중인 조사 전체(유형별로 동시에 있을 수 있음) */
    List<Inventory> findAllByStatus(Inventory.Status status);
}
