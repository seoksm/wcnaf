package com.winitech.smartAsset.infrastructure.assetHistory;

import com.winitech.smartAsset.domain.assetHistory.AssetHistory;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AssetHistoryStoreImpl implements AssetHistoryStore {

    private final AssetHistoryRepository assetHistoryRepository;

    @Override
    public void store(AssetHistory assetHistory) {
        assetHistoryRepository.save(assetHistory);
    }
}
