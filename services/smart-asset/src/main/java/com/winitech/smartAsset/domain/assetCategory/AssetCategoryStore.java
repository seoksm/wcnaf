package com.winitech.smartAsset.domain.assetCategory;

import java.util.UUID;

public interface AssetCategoryStore {

    UUID store(AssetCategory assetCategory);

    void modify(AssetCategory assetCategory, AssetCategoryCommand.UpdateCommand updateCommand);

    void delete(AssetCategory assetCategory);
}
