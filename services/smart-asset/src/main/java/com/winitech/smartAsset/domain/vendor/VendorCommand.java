package com.winitech.smartAsset.domain.vendor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
public class VendorCommand {

    private String name;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String memo;

    public Vendor toEntity() {
        return Vendor.builder()
                .name(name)
                .contactName(contactName)
                .contactPhone(contactPhone)
                .contactEmail(contactEmail)
                .memo(memo)
                .build();
    }

    @Getter
    @Builder
    public static class UpdateCommand {
        @Setter
        private UUID vendorId;
        private String name;
        private String contactName;
        private String contactPhone;
        private String contactEmail;
        private String memo;
    }
}
