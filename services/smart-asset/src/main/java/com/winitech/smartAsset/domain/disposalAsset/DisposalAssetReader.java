package com.winitech.smartAsset.domain.disposalAsset;

import java.util.Optional;
import java.util.UUID;

public interface DisposalAssetReader {

    Optional<DisposalAsset> findByTangibleAssetId(UUID tangibleAssetId);
}
