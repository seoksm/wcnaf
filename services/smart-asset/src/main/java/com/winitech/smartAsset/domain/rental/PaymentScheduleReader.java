package com.winitech.smartAsset.domain.rental;

import java.util.List;
import java.util.UUID;

public interface PaymentScheduleReader {

    PaymentSchedule findById(UUID paymentScheduleId);

    List<PaymentSchedule> findAllByRentalAssetId(UUID rentalAssetId);
}
