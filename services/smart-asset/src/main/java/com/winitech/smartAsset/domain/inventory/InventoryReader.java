package com.winitech.smartAsset.domain.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface InventoryReader {

    Inventory findById(UUID inventoryId);

    Page<Inventory> findAll(Pageable pageable);

    /** S-306: 반복 시행 시 직전 조사를 복제/이월 안내에 사용 - 같은 유형의 가장 최근 종료된 조사 */
    java.util.Optional<Inventory> findMostRecentClosedByType(Inventory.InventoryType inventoryType);

    /** S-306: 이미 다음 회차가 시작돼 있으면 예정일 안내를 띄우지 않는다(I2) */
    boolean existsInProgressByType(Inventory.InventoryType inventoryType);

    /** S-700 전수조사 진행률 - 진행 중인 조사 전체 */
    List<Inventory> findAllInProgress();
}
