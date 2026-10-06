package com.winitech.smartAsset.infrastructure.depreciation;

import com.winitech.smartAsset.domain.depreciation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DepreciationStoreImpl implements DepreciationSnapshotStore, DepreciationConfirmationStore {

    private final DepreciationSnapshotRepository snapshotRepository;
    private final DepreciationConfirmationRepository confirmationRepository;

    @Override
    public void storeAll(List<DepreciationSnapshot> snapshots) {
        snapshotRepository.saveAll(snapshots);
    }

    @Override
    public void deleteByPeriod(int fiscalYear, DepreciationQuarter quarter) {
        snapshotRepository.deleteByPeriod(fiscalYear, quarter);
    }

    @Override
    public void store(DepreciationConfirmation confirmation) {
        confirmationRepository.save(confirmation);
    }
}
