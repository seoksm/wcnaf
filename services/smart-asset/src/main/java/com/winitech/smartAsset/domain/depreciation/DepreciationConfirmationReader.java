package com.winitech.smartAsset.domain.depreciation;

import java.util.List;
import java.util.Optional;

public interface DepreciationConfirmationReader {

    Optional<DepreciationConfirmation> findActiveByPeriod(int fiscalYear, DepreciationQuarter quarter);

    List<DepreciationConfirmation> findAll();
}
