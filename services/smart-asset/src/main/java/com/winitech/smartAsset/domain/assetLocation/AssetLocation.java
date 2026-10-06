package com.winitech.smartAsset.domain.assetLocation;

import com.winitech.common.domain.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class AssetLocation extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "asset_location_id")
    private UUID id;

    @NonNull
    private String locationName;

    private Integer sortSeq;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        ENABLE("활성화"),
        DISABLE("비활성화");
        private final String description;
    }

    @Builder
    public AssetLocation(
            @NonNull String locationName,
            Integer sortSeq
    ) {
        this.locationName = locationName;
        this.sortSeq = sortSeq;
        this.status = Status.ENABLE;
    }

    public void modify(AssetLocationCommand.UpdateCommand updateCommand) {
        this.locationName = updateCommand.getLocationName();
        this.sortSeq = updateCommand.getSortSeq();
    }

    public void delete() {
        this.status = Status.DISABLE;
    }
}
