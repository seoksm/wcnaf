package com.winitech.smartAsset.infrastructure.disposalAsset;

import com.winitech.smartAsset.domain.disposalAsset.DisposalAsset;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DisposalAssetReaderImpl implements DisposalAssetReader {

    private final DisposalAssetRepository disposalAssetRepository;

    @Override
    public Optional<DisposalAsset> findByTangibleAssetId(UUID tangibleAssetId) {
        return disposalAssetRepository.findByTangibleAssetId(tangibleAssetId);
    }
}
