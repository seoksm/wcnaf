package com.winitech.smartAsset.domain.intangibleAsset;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@ToString
public class IntangibleAssetCommand {

    private IntangibleAsset.Type intangibleType;
    private String name;
    private String issuer;
    private LocalDate registeredDate;
    private LocalDate expiryDate;
    private UUID ownerMemberId;
    private String alertDays;
    private String memo;

    public IntangibleAsset toEntity() {
        return IntangibleAsset.builder()
                .intangibleType(intangibleType)
                .name(name)
                .issuer(issuer)
                .registeredDate(registeredDate)
                .expiryDate(expiryDate)
                .ownerMemberId(ownerMemberId)
                .alertDays(alertDays)
                .memo(memo)
                .build();
    }

    @Getter
    @Builder
    public static class UpdateCommand {
        @Setter
        private UUID intangibleAssetId;
        private IntangibleAsset.Type intangibleType;
        private String name;
        private String issuer;
        private LocalDate registeredDate;
        private LocalDate expiryDate;
        private UUID ownerMemberId;
        private String alertDays;
        private String memo;
    }
}
