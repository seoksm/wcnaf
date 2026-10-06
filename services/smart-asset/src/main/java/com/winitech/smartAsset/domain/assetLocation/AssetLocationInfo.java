package com.winitech.smartAsset.domain.assetLocation;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AssetLocationInfo {

    private final UUID locationId;
    private final String locationName;
    private final Integer sortSeq;
    private final AssetLocation.Status status;

    public AssetLocationInfo(AssetLocation assetLocation) {
        this.locationId = assetLocation.getId();
        this.locationName = assetLocation.getLocationName();
        this.sortSeq = assetLocation.getSortSeq();
        this.status = assetLocation.getStatus();
    }
}
