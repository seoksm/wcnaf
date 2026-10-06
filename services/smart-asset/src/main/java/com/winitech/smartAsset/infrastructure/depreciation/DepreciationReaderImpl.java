package com.winitech.smartAsset.infrastructure.depreciation;

import com.winitech.smartAsset.domain.depreciation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DepreciationReaderImpl implements DepreciationSnapshotReader, DepreciationConfirmationReader {

    private final DepreciationSnapshotRepository snapshotRepository;
    private final DepreciationConfirmationRepository confirmationRepository;

    @Override
    public List<DepreciationSnapshot> findByPeriod(int fiscalYear, DepreciationQuarter quarter) {
        return snapshotRepository.findByPeriod(fiscalYear, quarter);
    }

    @Override
    public List<DepreciationSnapshot> findByAssetAndYear(UUID tangibleAssetId, int fiscalYear) {
        return snapshotRepository.findByAssetAndYear(tangibleAssetId, fiscalYear);
    }

    @Override
    public Optional<DepreciationConfirmation> findActiveByPeriod(int fiscalYear, DepreciationQuarter quarter) {
        return confirmationRepository.findActiveByPeriod(fiscalYear, quarter);
    }

    @Override
    public List<DepreciationConfirmation> findAll() {
        return confirmationRepository.findAllOrderByConfirmedAtDesc();
    }
}
