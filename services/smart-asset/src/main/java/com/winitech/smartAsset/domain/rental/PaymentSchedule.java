package com.winitech.smartAsset.domain.rental;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.InvalidParamException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * S-512 결제 스케줄 - 귀속월(accrualMonth) 1건당 1행. Q-53: 예상액·실제액을 분리해 입력받는다
 * (사용량 기반 계약은 매월 금액이 다르다).
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class PaymentSchedule extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "payment_schedule_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_asset_id")
    private RentalAsset rentalAsset;

    @NonNull
    private LocalDate accrualMonth;

    private LocalDate dueDate;
    private BigDecimal expectedAmount;
    private BigDecimal actualAmount;

    @NonNull
    private Boolean confirmedYn;

    @Builder
    public PaymentSchedule(@NonNull RentalAsset rentalAsset, @NonNull LocalDate accrualMonth,
                            LocalDate dueDate, BigDecimal expectedAmount) {
        this.rentalAsset = rentalAsset;
        this.accrualMonth = accrualMonth;
        this.dueDate = dueDate;
        this.expectedAmount = expectedAmount;
        this.confirmedYn = false;
    }

    public void modify(LocalDate dueDate, BigDecimal expectedAmount) {
        if (Boolean.TRUE.equals(this.confirmedYn)) {
            throw new InvalidParamException("이미 확정된 결제 항목은 수정할 수 없습니다.");
        }
        this.dueDate = dueDate;
        this.expectedAmount = expectedAmount;
    }

    public void confirmActual(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
        this.confirmedYn = true;
    }
}
