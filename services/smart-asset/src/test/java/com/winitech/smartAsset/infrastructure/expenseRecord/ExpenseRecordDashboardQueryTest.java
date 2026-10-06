package com.winitech.smartAsset.infrastructure.expenseRecord;

import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * S-700 월별 비용추이 위젯의 그룹핑 쿼리 회귀 테스트. accrual_month는 src_id와 무관하게
 * "전체" 합계를 반환하므로(대시보드는 자산 1건이 아니라 전사 합계가 필요), 다른 테스트/운영
 * 데이터와 절대 겹치지 않는 먼 미래 월(2099-01)을 써서 격리한다.
 */
@SpringBootTest
@Transactional
class ExpenseRecordDashboardQueryTest {

    @Autowired
    private ExpenseRecordRepository expenseRecordRepository;

    private static final LocalDate ISOLATED_MONTH = LocalDate.of(2099, 1, 1);

    @BeforeEach
    void setUp() {
        expenseRecordRepository.save(ExpenseRecord.builder()
                .srcType(ExpenseRecord.SrcType.RENTAL).srcId(UUID.randomUUID())
                .accrualMonth(ISOLATED_MONTH).amountType(ExpenseRecord.AmountType.EXPECTED)
                .amount(new BigDecimal("100000")).build());
        expenseRecordRepository.save(ExpenseRecord.builder()
                .srcType(ExpenseRecord.SrcType.RENTAL).srcId(UUID.randomUUID())
                .accrualMonth(ISOLATED_MONTH).amountType(ExpenseRecord.AmountType.ACTUAL)
                .amount(new BigDecimal("123000")).build());
        expenseRecordRepository.save(ExpenseRecord.builder()
                .srcType(ExpenseRecord.SrcType.LICENSE).srcId(UUID.randomUUID())
                .accrualMonth(ISOLATED_MONTH).amountType(ExpenseRecord.AmountType.EXPECTED)
                .amount(new BigDecimal("50000")).build());
    }

    @Test
    void sumGroupedFrom은_srcType_월_amountType_조합별로_합산한다() {
        List<Object[]> rows = expenseRecordRepository.sumGroupedFrom(ISOLATED_MONTH);

        assertThat(rows).hasSize(3);
        assertThat(rows).extracting(
                row -> (ExpenseRecord.SrcType) row[0],
                row -> (LocalDate) row[1],
                row -> (ExpenseRecord.AmountType) row[2],
                row -> (BigDecimal) row[3]
        ).containsExactlyInAnyOrder(
                tuple(ExpenseRecord.SrcType.RENTAL, ISOLATED_MONTH, ExpenseRecord.AmountType.EXPECTED, new BigDecimal("100000.00")),
                tuple(ExpenseRecord.SrcType.RENTAL, ISOLATED_MONTH, ExpenseRecord.AmountType.ACTUAL, new BigDecimal("123000.00")),
                tuple(ExpenseRecord.SrcType.LICENSE, ISOLATED_MONTH, ExpenseRecord.AmountType.EXPECTED, new BigDecimal("50000.00")));
    }

    @Test
    void fromMonth_이전_귀속월은_제외된다() {
        List<Object[]> rows = expenseRecordRepository.sumGroupedFrom(ISOLATED_MONTH.plusMonths(1));

        assertThat(rows).isEmpty();
    }
}
