package com.winitech.smartAsset.infrastructure.disposalAsset;

import com.winitech.smartAsset.domain.disposalAsset.DisposalAsset;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DisposalAssetStoreImpl implements DisposalAssetStore {

    private final DisposalAssetRepository disposalAssetRepository;

    @Override
    public DisposalAsset store(DisposalAsset disposalAsset) {
        return disposalAssetRepository.save(disposalAsset);
    }
}
