package com.winitech.smartAsset.infrastructure.assetAssignment;

import com.winitech.smartAsset.domain.assetAssignment.AssetAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetAssignmentRepository extends JpaRepository<AssetAssignment, UUID> {

    @Query("select a from AssetAssignment a " +
            "where a.tangibleAsset.id = :tangibleAssetId and a.releasedAt is null")
    Optional<AssetAssignment> findCurrentByTangibleAssetId(UUID tangibleAssetId);

    @Query("select a from AssetAssignment a " +
            "where a.tangibleAsset.id = :tangibleAssetId order by a.assignedAt desc")
    List<AssetAssignment> findAllByTangibleAssetId(UUID tangibleAssetId);
}
