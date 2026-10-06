package com.winitech.smartAsset.domain.rental;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class PaymentScheduleInfo {

    private final UUID paymentScheduleId;
    private final LocalDate accrualMonth;
    private final LocalDate dueDate;
    private final BigDecimal expectedAmount;
    private final BigDecimal actualAmount;
    private final boolean confirmedYn;

    public PaymentScheduleInfo(PaymentSchedule schedule) {
        this.paymentScheduleId = schedule.getId();
        this.accrualMonth = schedule.getAccrualMonth();
        this.dueDate = schedule.getDueDate();
        this.expectedAmount = schedule.getExpectedAmount();
        this.actualAmount = schedule.getActualAmount();
        this.confirmedYn = Boolean.TRUE.equals(schedule.getConfirmedYn());
    }
}
