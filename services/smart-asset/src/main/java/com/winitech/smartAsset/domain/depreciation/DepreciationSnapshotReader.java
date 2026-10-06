package com.winitech.smartAsset.domain.depreciation;

import java.util.List;
import java.util.UUID;

public interface DepreciationSnapshotReader {

    List<DepreciationSnapshot> findByPeriod(int fiscalYear, DepreciationQuarter quarter);

    List<DepreciationSnapshot> findByAssetAndYear(UUID tangibleAssetId, int fiscalYear);
}
