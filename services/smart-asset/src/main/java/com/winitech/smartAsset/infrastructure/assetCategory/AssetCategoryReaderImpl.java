package com.winitech.smartAsset.infrastructure.assetCategory;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssetCategoryReaderImpl implements AssetCategoryReader {

    private final AssetCategoryRepository assetCategoryRepository;

    @Override
    public AssetCategory findById(UUID categoryId) {
        return assetCategoryRepository.findById(categoryId).orElseThrow();
    }

    @Override
    public List<AssetCategory> findAllByContainsKeyword(String keyword) {
        return assetCategoryRepository.findAllByContainsKeyword(keyword);
    }

    @Override
    public boolean existsByCategoryCode(String categoryCode) {
        return assetCategoryRepository.existsByCategoryCodeAndStatus(categoryCode, AssetCategory.Status.ENABLE);
    }
}
