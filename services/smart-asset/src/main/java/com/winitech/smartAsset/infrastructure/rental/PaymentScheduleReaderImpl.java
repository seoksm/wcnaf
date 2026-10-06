package com.winitech.smartAsset.infrastructure.rental;

import com.winitech.smartAsset.domain.rental.PaymentSchedule;
import com.winitech.smartAsset.domain.rental.PaymentScheduleReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PaymentScheduleReaderImpl implements PaymentScheduleReader {

    private final PaymentScheduleRepository paymentScheduleRepository;

    @Override
    public PaymentSchedule findById(UUID paymentScheduleId) {
        return paymentScheduleRepository.findById(paymentScheduleId).orElseThrow();
    }

    @Override
    public List<PaymentSchedule> findAllByRentalAssetId(UUID rentalAssetId) {
        return paymentScheduleRepository.findAllByRentalAsset_IdOrderByAccrualMonthDesc(rentalAssetId);
    }
}
