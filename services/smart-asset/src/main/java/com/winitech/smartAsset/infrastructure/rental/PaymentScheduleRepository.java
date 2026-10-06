package com.winitech.smartAsset.infrastructure.rental;

import com.winitech.smartAsset.domain.rental.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, UUID> {

    List<PaymentSchedule> findAllByRentalAsset_IdOrderByAccrualMonthDesc(UUID rentalAssetId);
}
