package com.winitech.smartAsset.infrastructure.rental;

import com.winitech.smartAsset.domain.rental.PaymentSchedule;
import com.winitech.smartAsset.domain.rental.PaymentScheduleStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PaymentScheduleStoreImpl implements PaymentScheduleStore {

    private final PaymentScheduleRepository paymentScheduleRepository;

    @Override
    public PaymentSchedule store(PaymentSchedule paymentSchedule) {
        return paymentScheduleRepository.save(paymentSchedule);
    }

    @Override
    public void modify(PaymentSchedule paymentSchedule, LocalDate dueDate, BigDecimal expectedAmount) {
        paymentSchedule.modify(dueDate, expectedAmount);
    }

    @Override
    public void confirmActual(PaymentSchedule paymentSchedule, BigDecimal actualAmount) {
        paymentSchedule.confirmActual(actualAmount);
    }
}
