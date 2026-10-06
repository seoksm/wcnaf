package com.winitech.smartAsset.domain.assetAssignment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetAssignmentReader {

    Optional<AssetAssignment> findCurrentByTangibleAssetId(UUID tangibleAssetId);

    List<AssetAssignment> findAllByTangibleAssetId(UUID tangibleAssetId);
}
