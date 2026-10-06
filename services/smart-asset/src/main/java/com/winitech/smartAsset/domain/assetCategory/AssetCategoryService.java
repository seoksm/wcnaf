package com.winitech.smartAsset.domain.assetCategory;

import java.util.List;
import java.util.UUID;

public interface AssetCategoryService {

    UUID createCategory(AssetCategoryCommand command);

    void updateCategory(AssetCategoryCommand.UpdateCommand updateCommand);

    void deleteCategory(UUID categoryId);

    List<AssetCategoryInfo> loadCategoryList(String keyword);

    AssetCategoryInfo loadCategory(UUID categoryId);
}
