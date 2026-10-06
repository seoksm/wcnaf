package com.winitech.smartAsset.application.assetCategory;

import com.winitech.smartAsset.domain.assetCategory.AssetCategoryCommand;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryInfo;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetCategoryFacade {

    private final AssetCategoryService assetCategoryService;

    public UUID postCategory(AssetCategoryCommand command) {
        return assetCategoryService.createCategory(command);
    }

    public void reviseCategory(AssetCategoryCommand.UpdateCommand updateCommand) {
        assetCategoryService.updateCategory(updateCommand);
    }

    public void removeCategory(UUID categoryId) {
        assetCategoryService.deleteCategory(categoryId);
    }

    public List<AssetCategoryInfo> getCategoryList(String keyword) {
        return assetCategoryService.loadCategoryList(keyword);
    }

    public AssetCategoryInfo getCategory(UUID categoryId) {
        return assetCategoryService.loadCategory(categoryId);
    }
}
