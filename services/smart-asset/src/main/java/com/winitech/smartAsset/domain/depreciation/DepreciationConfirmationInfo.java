package com.winitech.smartAsset.domain.depreciation;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/** S-232 확정/해제 이력 1행 */
@Getter
public class DepreciationConfirmationInfo {

    private final UUID id;
    private final int fiscalYear;
    private final String quarter;
    private final OffsetDateTime confirmedAt;
    private final UUID confirmedBy;
    private final OffsetDateTime releasedAt;
    private final UUID releasedBy;
    private final String releaseReason;

    public DepreciationConfirmationInfo(DepreciationConfirmation confirmation) {
        this.id = confirmation.getId();
        this.fiscalYear = confirmation.getFiscalYear();
        this.quarter = confirmation.getQuarter().name();
        this.confirmedAt = confirmation.getConfirmedAt();
        this.confirmedBy = confirmation.getConfirmedBy();
        this.releasedAt = confirmation.getReleasedAt();
        this.releasedBy = confirmation.getReleasedBy();
        this.releaseReason = confirmation.getReleaseReason();
    }
}
