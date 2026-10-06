package com.winitech.smartAsset.domain.tangibleAsset;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;

import java.util.UUID;

public interface TangibleAssetStore {

    UUID store(TangibleAsset tangibleAsset);

    /**
     * @return 배정 정보가 변경되었는지 여부 (asset_assignment 이력 갱신 필요 판단용)
     */
    boolean modify(TangibleAsset tangibleAsset, TangibleAssetCommand.UpdateCommand updateCommand, AssetCategory category, AssetLocation location);
}
