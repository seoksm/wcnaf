package com.winitech.smartAsset.domain.assetLocation;

import java.util.List;
import java.util.UUID;

public interface AssetLocationReader {

    AssetLocation findById(UUID locationId);

    List<AssetLocation> findAllByContainsKeyword(String keyword);
}
