package com.winitech.smartAsset.domain.depreciation;

import java.util.List;

public interface DepreciationSnapshotStore {

    void storeAll(List<DepreciationSnapshot> snapshots);

    void deleteByPeriod(int fiscalYear, DepreciationQuarter quarter);
}
