package com.winitech.smartAsset.domain.license;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
public class LicenseCommand {

    private String name;
    private String memo;

    public License toEntity() {
        return License.builder()
                .name(name)
                .memo(memo)
                .build();
    }

    @Getter
    @Builder
    public static class UpdateCommand {
        @Setter
        private UUID licenseId;
        private String name;
        private String memo;
    }
}
