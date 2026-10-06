package com.winitech.smartAsset.domain.assetAssignment;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetAssignment extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "asset_assignment_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tangible_asset_id")
    private TangibleAsset tangibleAsset;

    @NonNull
    private UUID memberId;

    @NonNull
    private String assignType;

    @NonNull
    private OffsetDateTime assignedAt;

    private OffsetDateTime releasedAt;

    private UUID assignedBy;

    @Builder
    public AssetAssignment(TangibleAsset tangibleAsset, @NonNull UUID memberId, @NonNull String assignType, UUID assignedBy) {
        this.tangibleAsset = tangibleAsset;
        this.memberId = memberId;
        this.assignType = assignType;
        this.assignedAt = OffsetDateTime.now();
        this.assignedBy = assignedBy;
    }

    public void release() {
        this.releasedAt = OffsetDateTime.now();
    }
}
