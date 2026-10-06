package com.winitech.smartAsset.domain.expenseRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRecordReader {

    Optional<ExpenseRecord> findOne(ExpenseRecord.SrcType srcType, UUID srcId, LocalDate accrualMonth, ExpenseRecord.AmountType amountType);

    /** S-700 월별 비용 추이 - [0]=SrcType, [1]=accrual_month, [2]=AmountType, [3]=합계 */
    List<Object[]> sumGroupedFrom(LocalDate fromMonth);
}
