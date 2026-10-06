package com.winitech.smartAsset.domain.assetLocation;

import java.util.UUID;

public interface AssetLocationStore {

    UUID store(AssetLocation assetLocation);

    void modify(AssetLocation assetLocation, AssetLocationCommand.UpdateCommand updateCommand);

    void delete(AssetLocation assetLocation);
}
