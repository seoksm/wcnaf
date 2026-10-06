package com.winitech.smartAsset.domain.assetLocation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
public class AssetLocationCommand {

    private String locationName;
    private Integer sortSeq;

    public AssetLocation toEntity() {
        return AssetLocation.builder()
                .locationName(locationName)
                .sortSeq(sortSeq)
                .build();
    }

    @Getter
    @Builder
    public static class UpdateCommand {
        @Setter
        private UUID locationId;
        private String locationName;
        private Integer sortSeq;
    }
}
