package com.winitech.smartAsset.infrastructure.inventory;

import com.winitech.smartAsset.domain.inventory.InventoryResult;
import com.winitech.smartAsset.domain.inventory.InventoryTarget;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InventoryTargetRepository extends JpaRepository<InventoryTarget, UUID> {

    @EntityGraph(attributePaths = {"tangibleAsset", "tangibleAsset.category", "tangibleAsset.location"})
    List<InventoryTarget> findAllByInventory_Id(UUID inventoryId);

    long countByInventory_Id(UUID inventoryId);

    // 매핑된 경로(r.inventoryTarget...)만 타고 들어가는 형태로 써야 한다 - 명시적 join ... on 이나
    // enum 리터럴을 쿼리 문자열에 직접 넣는 방식은 이 프로젝트의 Hibernate(classic HQL 파서)에서
    // 부트스트랩 시점에 QuerySyntaxException으로 깨진다(바인딩 파라미터로 넘기면 문제없다).
    @Query("select case when count(r) > 0 then true else false end from InventoryResult r " +
            "where r.inventoryTarget.inventory.id = :previousInventoryId " +
            "and r.inventoryTarget.tangibleAsset.id = :tangibleAssetId " +
            "and r.closureAction = :closureAction")
    boolean existsCarriedOver(@Param("previousInventoryId") UUID previousInventoryId,
                              @Param("tangibleAssetId") UUID tangibleAssetId,
                              @Param("closureAction") InventoryResult.ClosureAction closureAction);
}
