package com.winitech.smartAsset.domain.inventory;

import java.util.List;
import java.util.UUID;

public interface InventoryResultReader {

    InventoryResult findById(UUID inventoryResultId);

    InventoryResult findByInventoryTargetId(UUID inventoryTargetId);

    /** S-303/304/308 - 조사 1건에 속한 모든 검수 결과를 대상 자산·책임자 정보와 함께 조회 */
    List<InventoryResult> findAllByInventoryId(UUID inventoryId);

    List<InventoryResult> findAllByInventoryIdAndStatus(UUID inventoryId, InventoryResult.Status status);

    /** S-310 "내 전수조사" - 본인이 배정받은, 진행 중인 조사의 대상 목록(I2: 항상 최대 1건의 조사) */
    List<InventoryResult> findAllByMemberIdAndInventoryStatus(UUID memberId, Inventory.Status status);
}
