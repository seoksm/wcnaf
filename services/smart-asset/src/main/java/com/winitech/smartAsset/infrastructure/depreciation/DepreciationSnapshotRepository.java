package com.winitech.smartAsset.infrastructure.depreciation;

import com.winitech.smartAsset.domain.depreciation.DepreciationQuarter;
import com.winitech.smartAsset.domain.depreciation.DepreciationSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DepreciationSnapshotRepository extends JpaRepository<DepreciationSnapshot, UUID> {

    @Query("select s from DepreciationSnapshot s where s.fiscalYear = :fiscalYear and s.quarter = :quarter")
    List<DepreciationSnapshot> findByPeriod(int fiscalYear, DepreciationQuarter quarter);

    @Query("select s from DepreciationSnapshot s where s.tangibleAsset.id = :tangibleAssetId and s.fiscalYear = :fiscalYear")
    List<DepreciationSnapshot> findByAssetAndYear(UUID tangibleAssetId, int fiscalYear);

    @Modifying
    @Query("delete from DepreciationSnapshot s where s.fiscalYear = :fiscalYear and s.quarter = :quarter")
    void deleteByPeriod(int fiscalYear, DepreciationQuarter quarter);
}
