package com.winitech.smartAsset.infrastructure.assetLocation;

import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationCommand;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssetLocationStoreImpl implements AssetLocationStore {

    private final AssetLocationRepository assetLocationRepository;

    @Override
    public UUID store(AssetLocation assetLocation) {
        return assetLocationRepository.save(assetLocation).getId();
    }

    @Override
    public void modify(AssetLocation assetLocation, AssetLocationCommand.UpdateCommand updateCommand) {
        assetLocation.modify(updateCommand);
    }

    @Override
    public void delete(AssetLocation assetLocation) {
        assetLocation.delete();
    }
}
