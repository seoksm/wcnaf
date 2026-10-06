package com.winitech.smartAsset.infrastructure.expenseRecord;

import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecord;
import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecordReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ExpenseRecordReaderImpl implements ExpenseRecordReader {

    private final ExpenseRecordRepository expenseRecordRepository;

    @Override
    public Optional<ExpenseRecord> findOne(ExpenseRecord.SrcType srcType, UUID srcId, LocalDate accrualMonth, ExpenseRecord.AmountType amountType) {
        return expenseRecordRepository.findBySrcTypeAndSrcIdAndAccrualMonthAndAmountType(srcType, srcId, accrualMonth, amountType);
    }

    @Override
    public List<Object[]> sumGroupedFrom(LocalDate fromMonth) {
        return expenseRecordRepository.sumGroupedFrom(fromMonth);
    }
}
