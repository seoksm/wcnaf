package com.winitech.smartAsset.domain.assetCategory;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssetCategoryServiceImpl extends EgovAbstractServiceImpl implements AssetCategoryService {

    private final AssetCategoryReader assetCategoryReader;
    private final AssetCategoryStore assetCategoryStore;
    private final TangibleAssetReader tangibleAssetReader;

    @Transactional
    @Override
    public UUID createCategory(AssetCategoryCommand command) {

        if (assetCategoryReader.existsByCategoryCode(command.getCategoryCode())) {
            throw new InvalidParamException("이미 존재하는 자산 종류 코드입니다.");
        }

        return assetCategoryStore.store(command.toEntity());
    }

    @Transactional
    @Override
    public void updateCategory(AssetCategoryCommand.UpdateCommand updateCommand) {

        AssetCategory assetCategory = assetCategoryReader.findById(updateCommand.getCategoryId());
        assetCategoryStore.modify(assetCategory, updateCommand);
    }

    @Transactional
    @Override
    public void deleteCategory(UUID categoryId) {

        AssetCategory assetCategory = assetCategoryReader.findById(categoryId);

        long usageCount = tangibleAssetReader.countByCategoryId(categoryId);
        if (usageCount > 0) {
            throw new InvalidParamException("자산 " + usageCount + "건이 이 종류를 사용 중입니다.");
        }

        assetCategoryStore.delete(assetCategory);
    }

    @Override
    public List<AssetCategoryInfo> loadCategoryList(String keyword) {

        return assetCategoryReader.findAllByContainsKeyword(keyword).stream()
                .map(AssetCategoryInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public AssetCategoryInfo loadCategory(UUID categoryId) {

        return new AssetCategoryInfo(assetCategoryReader.findById(categoryId));
    }
}
