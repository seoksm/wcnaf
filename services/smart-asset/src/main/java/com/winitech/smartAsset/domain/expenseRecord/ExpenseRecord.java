package com.winitech.smartAsset.domain.expenseRecord;

import com.winitech.common.domain.AbstractEntity;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Q-52 - 렌탈·라이선스가 공용으로 쓰는 지출 원장. 이 단계에서는 자체 화면·API가 없다 - 렌탈의
 * 결제 확정(PaymentSchedule.confirmActual)이 내부적으로 이 테이블에 반영해두고, Step 8
 * 대시보드가 소비할 조회 대상으로 미리 쌓아두는 용도다(src_type/src_id 폴리모픽 참조라 FK 없음).
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class ExpenseRecord extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "expense_record_id")
    private UUID id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private SrcType srcType;

    @NonNull
    private UUID srcId;

    @NonNull
    private LocalDate accrualMonth;

    @NonNull
    @Enumerated(EnumType.STRING)
    private AmountType amountType;

    @NonNull
    private BigDecimal amount;

    private String memo;

    @Getter
    @RequiredArgsConstructor
    public enum SrcType {
        RENTAL("렌탈"),
        LICENSE("라이선스");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum AmountType {
        EXPECTED("예상"),
        ACTUAL("실제");
        private final String description;
    }

    @Builder
    public ExpenseRecord(@NonNull SrcType srcType, @NonNull UUID srcId, @NonNull LocalDate accrualMonth,
                          @NonNull AmountType amountType, @NonNull BigDecimal amount, String memo) {
        this.srcType = srcType;
        this.srcId = srcId;
        this.accrualMonth = accrualMonth;
        this.amountType = amountType;
        this.amount = amount;
        this.memo = memo;
    }

    public void updateAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
