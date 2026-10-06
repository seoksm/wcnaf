package com.winitech.smartAsset.domain.rental;

import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecord;
import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecordReader;
import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecordStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RentalServiceImpl extends EgovAbstractServiceImpl implements RentalService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createAt");

    private final RentalReader rentalReader;
    private final RentalStore rentalStore;
    private final PaymentScheduleReader paymentScheduleReader;
    private final PaymentScheduleStore paymentScheduleStore;
    private final ExpenseRecordReader expenseRecordReader;
    private final ExpenseRecordStore expenseRecordStore;

    @Transactional
    @Override
    public UUID createRental(RentalCommand command) {
        return rentalStore.store(command.toEntity());
    }

    @Transactional
    @Override
    public void updateRental(RentalCommand.UpdateCommand updateCommand) {
        RentalAsset asset = rentalReader.findById(updateCommand.getRentalAssetId());
        rentalStore.modify(asset, updateCommand);
    }

    @Transactional
    @Override
    public void cancelRental(UUID rentalAssetId) {
        RentalAsset asset = rentalReader.findById(rentalAssetId);
        rentalStore.cancel(asset);
    }

    @Override
    public Page<RentalInfo> loadList(String keyword, Integer page, Integer size) {
        return rentalReader.findAll(keyword, toPageable(page, size)).map(RentalInfo::new);
    }

    @Override
    public RentalInfo loadRental(UUID rentalAssetId) {
        return new RentalInfo(rentalReader.findById(rentalAssetId));
    }

    @Override
    public List<PaymentScheduleInfo> loadPaymentSchedules(UUID rentalAssetId) {
        return paymentScheduleReader.findAllByRentalAssetId(rentalAssetId).stream()
                .map(PaymentScheduleInfo::new).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UUID addPaymentSchedule(UUID rentalAssetId, LocalDate accrualMonth, LocalDate dueDate, BigDecimal expectedAmount) {
        RentalAsset asset = rentalReader.findById(rentalAssetId);
        PaymentSchedule schedule = PaymentSchedule.builder()
                .rentalAsset(asset)
                .accrualMonth(accrualMonth)
                .dueDate(dueDate)
                .expectedAmount(expectedAmount)
                .build();
        return paymentScheduleStore.store(schedule).getId();
    }

    @Transactional
    @Override
    public void updatePaymentSchedule(UUID paymentScheduleId, LocalDate dueDate, BigDecimal expectedAmount) {
        PaymentSchedule schedule = paymentScheduleReader.findById(paymentScheduleId);
        paymentScheduleStore.modify(schedule, dueDate, expectedAmount);
    }

    @Transactional
    @Override
    public void confirmPaymentSchedule(UUID paymentScheduleId, BigDecimal actualAmount) {
        PaymentSchedule schedule = paymentScheduleReader.findById(paymentScheduleId);
        paymentScheduleStore.confirmActual(schedule, actualAmount);

        UUID rentalAssetId = schedule.getRentalAsset().getId();
        expenseRecordReader.findOne(ExpenseRecord.SrcType.RENTAL, rentalAssetId, schedule.getAccrualMonth(), ExpenseRecord.AmountType.ACTUAL)
                .ifPresentOrElse(
                        existing -> existing.updateAmount(actualAmount),
                        () -> expenseRecordStore.store(ExpenseRecord.builder()
                                .srcType(ExpenseRecord.SrcType.RENTAL)
                                .srcId(rentalAssetId)
                                .accrualMonth(schedule.getAccrualMonth())
                                .amountType(ExpenseRecord.AmountType.ACTUAL)
                                .amount(actualAmount)
                                .build())
                );
    }

    private Pageable toPageable(Integer page, Integer size) {
        int safePage = (page == null || page < 0) ? 0 : page;
        int safeSize = (size == null) ? DEFAULT_PAGE_SIZE : Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        return PageRequest.of(safePage, safeSize, DEFAULT_SORT);
    }
}
