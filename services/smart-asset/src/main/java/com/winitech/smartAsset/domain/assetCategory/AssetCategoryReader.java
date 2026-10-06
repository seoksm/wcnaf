package com.winitech.smartAsset.domain.assetCategory;

import java.util.List;
import java.util.UUID;

public interface AssetCategoryReader {

    AssetCategory findById(UUID categoryId);

    List<AssetCategory> findAllByContainsKeyword(String keyword);

    boolean existsByCategoryCode(String categoryCode);
}
