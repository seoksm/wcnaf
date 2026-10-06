package com.winitech.smartAsset.domain.rental;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.InvalidParamException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;

/** S-510~512 렌탈·구독 1건 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class RentalAsset extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "rental_asset_id")
    private UUID id;

    @NonNull
    private String name;

    @NonNull
    @Enumerated(EnumType.STRING)
    private BillingMode billingMode;

    @NonNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    private String memo;

    @Getter
    @RequiredArgsConstructor
    public enum BillingMode {
        FIXED("고정액"),
        USAGE_BASED("사용량기반");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        ACTIVE("구독중"),
        CANCELLED("해지");
        private final String description;
    }

    @Builder
    public RentalAsset(@NonNull String name, @NonNull BillingMode billingMode, @NonNull LocalDate startDate,
                        LocalDate endDate, String memo) {
        this.name = name;
        this.billingMode = billingMode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memo = memo;
        this.status = Status.ACTIVE;
    }

    public void modify(RentalCommand.UpdateCommand command) {
        this.name = command.getName();
        this.billingMode = command.getBillingMode();
        this.startDate = command.getStartDate();
        this.endDate = command.getEndDate();
        this.memo = command.getMemo();
    }

    /** 해지는 삭제가 아니다 - 지출 이력은 그대로 보존된다(설계문서 §2) */
    public void cancel() {
        if (this.status == Status.CANCELLED) {
            throw new InvalidParamException("이미 해지된 렌탈입니다.");
        }
        this.status = Status.CANCELLED;
    }
}
