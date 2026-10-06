package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TangibleAssetStoreImpl implements TangibleAssetStore {

    private final TangibleAssetRepository tangibleAssetRepository;

    @Override
    public UUID store(TangibleAsset tangibleAsset) {
        return tangibleAssetRepository.save(tangibleAsset).getId();
    }

    @Override
    public boolean modify(TangibleAsset tangibleAsset, TangibleAssetCommand.UpdateCommand updateCommand, AssetCategory category, AssetLocation location) {
        return tangibleAsset.modify(updateCommand, category, location);
    }
}
