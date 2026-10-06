package com.winitech.smartAsset.domain.assetLocation;

import java.util.List;
import java.util.UUID;

public interface AssetLocationService {

    UUID createLocation(AssetLocationCommand command);

    void updateLocation(AssetLocationCommand.UpdateCommand updateCommand);

    void deleteLocation(UUID locationId);

    List<AssetLocationInfo> loadLocationList(String keyword);

    AssetLocationInfo loadLocation(UUID locationId);
}
