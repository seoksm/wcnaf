package com.winitech.smartAsset.application.depreciation;

import com.winitech.smartAsset.domain.depreciation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepreciationFacade {

    private final DepreciationService depreciationService;

    public DepreciationStatusInfo getStatus(int fiscalYear, String quarter) {
        return depreciationService.loadStatus(fiscalYear, DepreciationQuarter.valueOf(quarter));
    }

    public List<DepreciationScheduleRowInfo> getSchedule(UUID tangibleAssetId, int fiscalYear) {
        return depreciationService.loadSchedule(tangibleAssetId, fiscalYear);
    }

    public void confirm(int fiscalYear, String quarter) {
        depreciationService.confirm(fiscalYear, DepreciationQuarter.valueOf(quarter));
    }

    public void release(int fiscalYear, String quarter, String reason) {
        depreciationService.release(fiscalYear, DepreciationQuarter.valueOf(quarter), reason);
    }

    public List<DepreciationConfirmationInfo> getConfirmationLog() {
        return depreciationService.loadConfirmationLog();
    }
}
