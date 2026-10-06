package com.winitech.smartAsset.application.rental;

import com.winitech.smartAsset.domain.rental.PaymentScheduleInfo;
import com.winitech.smartAsset.domain.rental.RentalCommand;
import com.winitech.smartAsset.domain.rental.RentalInfo;
import com.winitech.smartAsset.domain.rental.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalFacade {

    private final RentalService rentalService;

    public UUID postRental(RentalCommand command) {
        return rentalService.createRental(command);
    }

    public void reviseRental(RentalCommand.UpdateCommand updateCommand) {
        rentalService.updateRental(updateCommand);
    }

    public void cancelRental(UUID rentalAssetId) {
        rentalService.cancelRental(rentalAssetId);
    }

    public Page<RentalInfo> getList(String keyword, Integer page, Integer size) {
        return rentalService.loadList(keyword, page, size);
    }

    public RentalInfo getRental(UUID rentalAssetId) {
        return rentalService.loadRental(rentalAssetId);
    }

    public List<PaymentScheduleInfo> getPaymentSchedules(UUID rentalAssetId) {
        return rentalService.loadPaymentSchedules(rentalAssetId);
    }

    public UUID addPaymentSchedule(UUID rentalAssetId, LocalDate accrualMonth, LocalDate dueDate, BigDecimal expectedAmount) {
        return rentalService.addPaymentSchedule(rentalAssetId, accrualMonth, dueDate, expectedAmount);
    }

    public void updatePaymentSchedule(UUID paymentScheduleId, LocalDate dueDate, BigDecimal expectedAmount) {
        rentalService.updatePaymentSchedule(paymentScheduleId, dueDate, expectedAmount);
    }

    public void confirmPaymentSchedule(UUID paymentScheduleId, BigDecimal actualAmount) {
        rentalService.confirmPaymentSchedule(paymentScheduleId, actualAmount);
    }
}
