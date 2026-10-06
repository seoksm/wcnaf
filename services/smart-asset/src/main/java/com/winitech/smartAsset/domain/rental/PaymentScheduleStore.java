package com.winitech.smartAsset.domain.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PaymentScheduleStore {

    PaymentSchedule store(PaymentSchedule paymentSchedule);

    void modify(PaymentSchedule paymentSchedule, LocalDate dueDate, BigDecimal expectedAmount);

    void confirmActual(PaymentSchedule paymentSchedule, BigDecimal actualAmount);
}
