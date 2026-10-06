package com.winitech.smartAsset.domain.depreciation;

import com.winitech.common.domain.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/** 결산 확정/해제 이력 (S-232, D7) - 분기당 releasedAt이 null인 행이 최대 1건(현재 활성 확정) */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepreciationConfirmation extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "depreciation_confirmation_id")
    private UUID id;

    @NonNull
    private Integer fiscalYear;

    @NonNull
    @Enumerated(EnumType.STRING)
    private DepreciationQuarter quarter;

    @NonNull
    private OffsetDateTime confirmedAt;

    private UUID confirmedBy;

    private OffsetDateTime releasedAt;

    private UUID releasedBy;

    private String releaseReason;

    @Builder
    public DepreciationConfirmation(@NonNull Integer fiscalYear, @NonNull DepreciationQuarter quarter, UUID confirmedBy) {
        this.fiscalYear = fiscalYear;
        this.quarter = quarter;
        this.confirmedAt = OffsetDateTime.now();
        this.confirmedBy = confirmedBy;
    }

    public void release(UUID releasedBy, String reason) {
        this.releasedAt = OffsetDateTime.now();
        this.releasedBy = releasedBy;
        this.releaseReason = reason;
    }
}
