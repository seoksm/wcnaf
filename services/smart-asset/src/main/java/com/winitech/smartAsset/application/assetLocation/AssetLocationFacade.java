package com.winitech.smartAsset.application.assetLocation;

import com.winitech.smartAsset.domain.assetLocation.AssetLocationCommand;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationInfo;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetLocationFacade {

    private final AssetLocationService assetLocationService;

    public UUID postLocation(AssetLocationCommand command) {
        return assetLocationService.createLocation(command);
    }

    public void reviseLocation(AssetLocationCommand.UpdateCommand updateCommand) {
        assetLocationService.updateLocation(updateCommand);
    }

    public void removeLocation(UUID locationId) {
        assetLocationService.deleteLocation(locationId);
    }

    public List<AssetLocationInfo> getLocationList(String keyword) {
        return assetLocationService.loadLocationList(keyword);
    }

    public AssetLocationInfo getLocation(UUID locationId) {
        return assetLocationService.loadLocation(locationId);
    }
}
