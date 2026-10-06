package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.Inventory;
import com.winitech.smartAsset.domain.inventory.InventoryReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryReaderImpl implements InventoryReader {

    private final InventoryRepository inventoryRepository;

    @Override
    public Inventory findById(UUID inventoryId) {
        return inventoryRepository.findById(inventoryId).orElseThrow();
    }

    @Override
    public Page<Inventory> findAll(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    @Override
    public Optional<Inventory> findMostRecentClosedByType(Inventory.InventoryType inventoryType) {
        return inventoryRepository.findFirstByInventoryTypeAndStatusOrderByClosedAtDesc(inventoryType, Inventory.Status.CLOSED);
    }

    @Override
    public boolean existsInProgressByType(Inventory.InventoryType inventoryType) {
        return inventoryRepository.existsByInventoryTypeAndStatus(inventoryType, Inventory.Status.IN_PROGRESS);
    }

    @Override
    public List<Inventory> findAllInProgress() {
        return inventoryRepository.findAllByStatus(Inventory.Status.IN_PROGRESS);
    }
}
