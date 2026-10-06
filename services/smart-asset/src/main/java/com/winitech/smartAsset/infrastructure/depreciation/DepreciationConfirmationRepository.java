package com.winitech.smartAsset.infrastructure.depreciation;

import com.winitech.smartAsset.domain.depreciation.DepreciationConfirmation;
import com.winitech.smartAsset.domain.depreciation.DepreciationQuarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepreciationConfirmationRepository extends JpaRepository<DepreciationConfirmation, UUID> {

    @Query("select c from DepreciationConfirmation c " +
            "where c.fiscalYear = :fiscalYear and c.quarter = :quarter and c.releasedAt is null")
    Optional<DepreciationConfirmation> findActiveByPeriod(int fiscalYear, DepreciationQuarter quarter);

    @Query("select c from DepreciationConfirmation c order by c.confirmedAt desc")
    List<DepreciationConfirmation> findAllOrderByConfirmedAtDesc();
}
