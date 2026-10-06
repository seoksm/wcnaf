package com.winitech.smartAsset.infrastructure.expenseRecord;

import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecord;
import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecordStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ExpenseRecordStoreImpl implements ExpenseRecordStore {

    private final ExpenseRecordRepository expenseRecordRepository;

    @Override
    public ExpenseRecord store(ExpenseRecord expenseRecord) {
        return expenseRecordRepository.save(expenseRecord);
    }
}
