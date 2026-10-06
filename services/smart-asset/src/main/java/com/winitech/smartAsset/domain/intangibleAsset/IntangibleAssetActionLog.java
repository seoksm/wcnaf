package com.winitech.smartAsset.domain.intangibleAsset;

import com.winitech.common.domain.AbstractEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/** S-502 갱신 이력 - append-only */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntangibleAssetActionLog extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "intangible_asset_action_log_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intangible_asset_id")
    private IntangibleAsset intangibleAsset;

    @NonNull
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private LocalDate previousExpiryDate;
    private LocalDate newExpiryDate;
    private String note;

    @NonNull
    private UUID actedBy;

    @NonNull
    private OffsetDateTime actedAt;

    @Getter
    @RequiredArgsConstructor
    public enum ActionType {
        RENEW("갱신");
        private final String description;
    }

    @Builder
    public IntangibleAssetActionLog(@NonNull IntangibleAsset intangibleAsset, @NonNull ActionType actionType,
                                     LocalDate previousExpiryDate, LocalDate newExpiryDate, String note,
                                     @NonNull UUID actedBy) {
        this.intangibleAsset = intangibleAsset;
        this.actionType = actionType;
        this.previousExpiryDate = previousExpiryDate;
        this.newExpiryDate = newExpiryDate;
        this.note = note;
        this.actedBy = actedBy;
        this.actedAt = OffsetDateTime.now();
    }
}
