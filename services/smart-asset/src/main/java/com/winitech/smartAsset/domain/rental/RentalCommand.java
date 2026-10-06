package com.winitech.smartAsset.domain.rental;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@ToString
public class RentalCommand {

    private String name;
    private RentalAsset.BillingMode billingMode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String memo;

    public RentalAsset toEntity() {
        return RentalAsset.builder()
                .name(name)
                .billingMode(billingMode)
                .startDate(startDate)
                .endDate(endDate)
                .memo(memo)
                .build();
    }

    @Getter
    @Builder
    public static class UpdateCommand {
        @Setter
        private UUID rentalAssetId;
        private String name;
        private RentalAsset.BillingMode billingMode;
        private LocalDate startDate;
        private LocalDate endDate;
        private String memo;
    }
}
