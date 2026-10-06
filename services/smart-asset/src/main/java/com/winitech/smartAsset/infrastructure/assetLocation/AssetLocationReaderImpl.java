package com.winitech.smartAsset.infrastructure.assetLocation;

import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssetLocationReaderImpl implements AssetLocationReader {

    private final AssetLocationRepository assetLocationRepository;

    @Override
    public AssetLocation findById(UUID locationId) {
        return assetLocationRepository.findById(locationId).orElseThrow();
    }

    @Override
    public List<AssetLocation> findAllByContainsKeyword(String keyword) {
        return assetLocationRepository.findAllByContainsKeyword(keyword);
    }
}
