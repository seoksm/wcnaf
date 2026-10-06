package com.winitech.smartAsset.infrastructure.assetCategory;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryCommand;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssetCategoryStoreImpl implements AssetCategoryStore {

    private final AssetCategoryRepository assetCategoryRepository;

    @Override
    public UUID store(AssetCategory assetCategory) {
        return assetCategoryRepository.save(assetCategory).getId();
    }

    @Override
    public void modify(AssetCategory assetCategory, AssetCategoryCommand.UpdateCommand updateCommand) {
        assetCategory.modify(updateCommand);
    }

    @Override
    public void delete(AssetCategory assetCategory) {
        assetCategory.delete();
    }
}
