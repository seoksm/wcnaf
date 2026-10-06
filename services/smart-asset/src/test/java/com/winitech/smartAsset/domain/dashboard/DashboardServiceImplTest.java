package com.winitech.smartAsset.domain.dashboard;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.smartAsset.domain.depreciation.DepreciationService;
import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecord;
import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecordReader;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetReader;
import com.winitech.smartAsset.domain.inventory.InventoryReader;
import com.winitech.smartAsset.domain.inventory.InventoryService;
import com.winitech.smartAsset.domain.license.LicenseAssignedUserReader;
import com.winitech.smartAsset.domain.license.LicensePurchaseRecordReader;
import com.winitech.smartAsset.domain.loan.LoanReader;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import com.winitech.smartAsset.domain.ticket.TicketReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

/**
 * S-700 대시보드 집계 중, 손으로 짠 판단 로직(V4 7범주 상한, Q-53 실제액 우선, Q-47 정합성 비교)만
 * 골라 검증한다. 단순 위임 메서드(레포지토리 그대로 호출)는 커버하지 않는다 - 그 부분이 깨지면
 * 레포지토리 계층의 통합 테스트나 실제 API 호출에서 드러난다.
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock private TangibleAssetReader tangibleAssetReader;
    @Mock private DepreciationService depreciationService;
    @Mock private ExpenseRecordReader expenseRecordReader;
    @Mock private IntangibleAssetReader intangibleAssetReader;
    @Mock private InventoryReader inventoryReader;
    @Mock private InventoryService inventoryService;
    @Mock private TicketReader ticketReader;
    @Mock private LoanReader loanReader;
    @Mock private LicenseAssignedUserReader licenseAssignedUserReader;
    @Mock private LicensePurchaseRecordReader licensePurchaseRecordReader;
    @Mock private DashboardWidgetConfigReader dashboardWidgetConfigReader;
    @Mock private DashboardWidgetConfigStore dashboardWidgetConfigStore;
    @Mock private LoginUserContext loginUserContext;

    private DashboardServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DashboardServiceImpl(
                tangibleAssetReader, depreciationService, expenseRecordReader, intangibleAssetReader,
                inventoryReader, inventoryService, ticketReader, loanReader, licenseAssignedUserReader,
                licensePurchaseRecordReader, dashboardWidgetConfigReader, dashboardWidgetConfigStore, loginUserContext);
    }

    @Test
    void capTop은_7개_이하면_그대로_둔다() {
        List<DashboardSummaryInfo.NameCount> rows = Arrays.asList(
                new DashboardSummaryInfo.NameCount("노트북", 10),
                new DashboardSummaryInfo.NameCount("모니터", 5));

        List<DashboardSummaryInfo.NameCount> result = DashboardServiceImpl.capTop(rows);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("노트북");
    }

    @Test
    void capTop은_7개_초과면_상위6과_기타로_접는다() {
        List<DashboardSummaryInfo.NameCount> rows = Arrays.asList(
                new DashboardSummaryInfo.NameCount("A", 70), new DashboardSummaryInfo.NameCount("B", 60),
                new DashboardSummaryInfo.NameCount("C", 50), new DashboardSummaryInfo.NameCount("D", 40),
                new DashboardSummaryInfo.NameCount("E", 30), new DashboardSummaryInfo.NameCount("F", 20),
                new DashboardSummaryInfo.NameCount("G", 3), new DashboardSummaryInfo.NameCount("H", 2));

        List<DashboardSummaryInfo.NameCount> result = DashboardServiceImpl.capTop(rows);

        assertThat(result).hasSize(7);
        assertThat(result.subList(0, 6)).extracting(DashboardSummaryInfo.NameCount::getName)
                .containsExactly("A", "B", "C", "D", "E", "F");
        assertThat(result.get(6).getName()).isEqualTo("기타");
        assertThat(result.get(6).getCount()).isEqualTo(5L);
    }

    @Test
    void pickAmount은_실제액이_있으면_실제액을_우선한다() {
        java.util.Map<ExpenseRecord.AmountType, BigDecimal> byType = new java.util.HashMap<>();
        byType.put(ExpenseRecord.AmountType.EXPECTED, new BigDecimal("100"));
        byType.put(ExpenseRecord.AmountType.ACTUAL, new BigDecimal("123"));

        assertThat(DashboardServiceImpl.pickAmount(byType)).isEqualTo(new BigDecimal("123"));
    }

    @Test
    void pickAmount은_실제액이_없으면_예상액을_쓴다() {
        java.util.Map<ExpenseRecord.AmountType, BigDecimal> byType = new java.util.HashMap<>();
        byType.put(ExpenseRecord.AmountType.EXPECTED, new BigDecimal("100"));

        assertThat(DashboardServiceImpl.pickAmount(byType)).isEqualTo(new BigDecimal("100"));
    }

    @Test
    void pickAmount은_둘다_없으면_0이다() {
        assertThat(DashboardServiceImpl.pickAmount(null)).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void 라이선스_정합성은_구매내역이_없는_라이선스도_초과판정_대상에_포함한다() {
        UUID licenseWithoutPurchase = UUID.randomUUID();
        UUID licenseWithinCapacity = UUID.randomUUID();

        when(licensePurchaseRecordReader.sumQuantityGroupByLicense())
                .thenReturn(List.<Object[]>of(new Object[]{licenseWithinCapacity, 10L}));
        when(licenseAssignedUserReader.countActiveGroupByLicense())
                .thenReturn(List.<Object[]>of(
                        new Object[]{licenseWithoutPurchase, 3L},
                        new Object[]{licenseWithinCapacity, 4L}));
        when(licenseAssignedUserReader.countUnlinkedActive()).thenReturn(2L);

        DashboardSummaryInfo.LicenseConsistency result = service.loadLicenseConsistency();

        assertThat(result.getOverCapacityLicenseCount()).isEqualTo(1);
        assertThat(result.getUnlinkedAssignmentCount()).isEqualTo(2);
    }

    @Test
    void 티켓현황은_접수대기를_WAITING과_RECEIVED_합계로_계산한다() {
        when(ticketReader.countByStatusIn(anyCollection())).thenReturn(5L, 2L);
        when(ticketReader.countOverdue(any())).thenReturn(1L);
        when(ticketReader.countCompletedSince(any())).thenReturn(3L);

        DashboardSummaryInfo.TicketStatus result = service.loadTicketStatus(LocalDate.now());

        assertThat(result.getWaitingCount()).isEqualTo(5L);
        assertThat(result.getInProgressCount()).isEqualTo(2L);
        assertThat(result.getOverdueCount()).isEqualTo(1L);
        assertThat(result.getDoneThisWeekCount()).isEqualTo(3L);
    }

    @Test
    void 월별비용추이는_최근_12개월을_오래된순으로_반환한다() {
        when(expenseRecordReader.sumGroupedFrom(any())).thenReturn(List.of());

        List<DashboardSummaryInfo.MonthlyCost> result = service.loadMonthlyCostTrend(LocalDate.of(2026, 9, 21));

        assertThat(result).hasSize(12);
        assertThat(result.get(0).getMonth()).isEqualTo(LocalDate.of(2025, 10, 1));
        assertThat(result.get(11).getMonth()).isEqualTo(LocalDate.of(2026, 9, 1));
        assertThat(result.get(0).getRentalAmount()).isEqualTo(BigDecimal.ZERO);
    }
}
