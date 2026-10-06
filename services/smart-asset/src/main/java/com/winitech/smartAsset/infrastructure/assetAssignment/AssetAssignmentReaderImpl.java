package com.winitech.smartAsset.infrastructure.assetAssignment;

import com.winitech.smartAsset.domain.assetAssignment.AssetAssignment;
import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AssetAssignmentReaderImpl implements AssetAssignmentReader {

    private final AssetAssignmentRepository assetAssignmentRepository;

    @Override
    public Optional<AssetAssignment> findCurrentByTangibleAssetId(UUID tangibleAssetId) {
        return assetAssignmentRepository.findCurrentByTangibleAssetId(tangibleAssetId);
    }

    @Override
    public List<AssetAssignment> findAllByTangibleAssetId(UUID tangibleAssetId) {
        return assetAssignmentRepository.findAllByTangibleAssetId(tangibleAssetId);
    }
}
