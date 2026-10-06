package com.winitech.smartAsset.infrastructure.expenseRecord;

import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRecordRepository extends JpaRepository<ExpenseRecord, UUID> {

    Optional<ExpenseRecord> findBySrcTypeAndSrcIdAndAccrualMonthAndAmountType(
            ExpenseRecord.SrcType srcType, UUID srcId, LocalDate accrualMonth, ExpenseRecord.AmountType amountType);

    /**
     * S-700 월별 비용 추이 - fromMonth 이후 귀속월 전체를 src_type·accrual_month·amount_type
     * 조합별로 합산한다. 같은 (src_type, accrual_month)에 EXPECTED·ACTUAL 행이 함께 있을 수
     * 있어(Q-53) 어느 쪽을 쓸지는 이 쿼리가 정하지 않고 Service에서 실제액 우선으로 고른다.
     * [0]=SrcType, [1]=accrual_month(LocalDate), [2]=AmountType, [3]=합계(BigDecimal)
     */
    @Query("select e.srcType, e.accrualMonth, e.amountType, sum(e.amount) from ExpenseRecord e " +
            "where e.accrualMonth >= :fromMonth " +
            "group by e.srcType, e.accrualMonth, e.amountType")
    List<Object[]> sumGroupedFrom(LocalDate fromMonth);
}
