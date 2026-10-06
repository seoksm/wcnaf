package com.winitech.smartAsset.infrastructure.disposalAsset;

import com.winitech.smartAsset.domain.disposalAsset.DisposalAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface DisposalAssetRepository extends JpaRepository<DisposalAsset, UUID> {

    @Query("select d from DisposalAsset d where d.tangibleAsset.id = :tangibleAssetId")
    Optional<DisposalAsset> findByTangibleAssetId(UUID tangibleAssetId);
}
