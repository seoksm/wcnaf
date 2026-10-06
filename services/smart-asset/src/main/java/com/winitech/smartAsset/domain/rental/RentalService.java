package com.winitech.smartAsset.domain.rental;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RentalService {

    UUID createRental(RentalCommand command);

    void updateRental(RentalCommand.UpdateCommand updateCommand);

    void cancelRental(UUID rentalAssetId);

    Page<RentalInfo> loadList(String keyword, Integer page, Integer size);

    RentalInfo loadRental(UUID rentalAssetId);

    List<PaymentScheduleInfo> loadPaymentSchedules(UUID rentalAssetId);

    UUID addPaymentSchedule(UUID rentalAssetId, LocalDate accrualMonth, LocalDate dueDate, BigDecimal expectedAmount);

    void updatePaymentSchedule(UUID paymentScheduleId, LocalDate dueDate, BigDecimal expectedAmount);

    /** 실제금액 확정 - Q-52에 따라 expense_record(ACTUAL)도 함께 upsert한다 */
    void confirmPaymentSchedule(UUID paymentScheduleId, BigDecimal actualAmount);
}
