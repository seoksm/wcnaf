package com.winitech.smartAsset.domain.rental;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class RentalInfo {

    private final UUID rentalAssetId;
    private final String name;
    private final RentalAsset.BillingMode billingMode;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final RentalAsset.Status status;
    private final String memo;

    public RentalInfo(RentalAsset rentalAsset) {
        this.rentalAssetId = rentalAsset.getId();
        this.name = rentalAsset.getName();
        this.billingMode = rentalAsset.getBillingMode();
        this.startDate = rentalAsset.getStartDate();
        this.endDate = rentalAsset.getEndDate();
        this.status = rentalAsset.getStatus();
        this.memo = rentalAsset.getMemo();
    }
}
