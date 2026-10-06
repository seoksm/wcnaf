package com.winitech.smartAsset.domain.dashboard;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.smartAsset.domain.depreciation.DepreciationQuarter;
import com.winitech.smartAsset.domain.depreciation.DepreciationService;
import com.winitech.smartAsset.domain.depreciation.DepreciationStatusInfo;
import com.winitech.smartAsset.domain.depreciation.DepreciationSummaryInfo;
import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecord;
import com.winitech.smartAsset.domain.expenseRecord.ExpenseRecordReader;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAsset;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetReader;
import com.winitech.smartAsset.domain.inventory.Inventory;
import com.winitech.smartAsset.domain.inventory.InventoryProgressInfo;
import com.winitech.smartAsset.domain.inventory.InventoryReader;
import com.winitech.smartAsset.domain.inventory.InventoryService;
import com.winitech.smartAsset.domain.license.LicenseAssignedUserReader;
import com.winitech.smartAsset.domain.license.LicensePurchaseRecordReader;
import com.winitech.smartAsset.domain.loan.LoanReader;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import com.winitech.smartAsset.domain.ticket.Ticket;
import com.winitech.smartAsset.domain.ticket.TicketReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * S-700 대시보드 집계 - 캐시를 두지 않고 매 요청 실시간으로 계산한다(설계문서 §2 "성능" 항목은
 * 5분 캐시를 권장했으나, 이 규모(자산 수천 건)에서는 요청마다 재계산해도 체감 지연이 크지 않다고
 * 판단해 캐시 없이 진행한다 - 캐시 무효화 로직 자체가 없어 구현이 단순해지는 이점도 있다).
 *
 * 가능한 한 각 도메인이 이미 가진 계산 로직을 그대로 재사용한다 - 특히 자산 총계(KPI)는
 * DepreciationService.loadStatus를 그대로 호출해 S-230 화면과 항상 같은 숫자를 보여준다
 * (대시보드가 별도로 계산하면 두 화면 숫자가 어긋날 수 있다).
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    /** V4: 범주가 7개를 넘으면 상위 6 + "기타"로 접는다 */
    private static final int TOP_CATEGORY_COUNT = 6;
    private static final String OTHER_LABEL = "기타";
    /** 월별 비용추이 - 이번 달 포함 최근 12개월 */
    private static final int TREND_MONTHS = 12;
    /** "이번주완료"는 칸반(S-600)이 이미 쓰는 "완료 후 최근 N일" 관례를 그대로 재사용한다 */
    private static final int RECENT_DONE_WINDOW_DAYS = 7;

    private final TangibleAssetReader tangibleAssetReader;
    private final DepreciationService depreciationService;
    private final ExpenseRecordReader expenseRecordReader;
    private final IntangibleAssetReader intangibleAssetReader;
    private final InventoryReader inventoryReader;
    private final InventoryService inventoryService;
    private final TicketReader ticketReader;
    private final LoanReader loanReader;
    private final LicenseAssignedUserReader licenseAssignedUserReader;
    private final LicensePurchaseRecordReader licensePurchaseRecordReader;
    private final DashboardWidgetConfigReader dashboardWidgetConfigReader;
    private final DashboardWidgetConfigStore dashboardWidgetConfigStore;
    private final LoginUserContext loginUserContext;

    @Override
    public DashboardSummaryInfo loadSummary() {
        LocalDate today = LocalDate.now();

        return new DashboardSummaryInfo(
                OffsetDateTime.now(),
                loadAssetSummary(today),
                capTop(toNameCounts(tangibleAssetReader.countActiveGroupByCategory())),
                capTop(toNameCounts(tangibleAssetReader.countActiveGroupByLocation())),
                loadStatusDistribution(),
                loadMonthlyCostTrend(today),
                loadExpiringSoon(today),
                loadTicketStatus(today),
                new DashboardSummaryInfo.LoanStatus(loanReader.countActive(), loanReader.countOverdueActive(today)),
                loadInventoryProgress(),
                loadLicenseConsistency());
    }

    private DashboardSummaryInfo.AssetSummary loadAssetSummary(LocalDate today) {
        DepreciationStatusInfo status = depreciationService.loadStatus(today.getYear(), quarterOf(today.getMonthValue()));
        DepreciationSummaryInfo summary = status.getSummary();
        OffsetDateTime basedOn = summary.isConfirmed() ? summary.getConfirmedAt() : summary.getCalculatedAt();
        return new DashboardSummaryInfo.AssetSummary(
                status.getRows().size(),
                summary.getTotalAcquisitionAmount(),
                summary.getTotalClosingAccumulated(),
                summary.getTotalBookValue(),
                summary.isConfirmed(),
                basedOn);
    }

    private static DepreciationQuarter quarterOf(int month) {
        if (month <= 3) return DepreciationQuarter.Q1;
        if (month <= 6) return DepreciationQuarter.Q2;
        if (month <= 9) return DepreciationQuarter.Q3;
        return DepreciationQuarter.Q4;
    }

    private static List<DashboardSummaryInfo.NameCount> toNameCounts(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new DashboardSummaryInfo.NameCount((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }

    /** V4: 이미 count desc로 정렬돼 들어온다(레포지토리 쿼리) - 7개 초과분만 "기타"로 합친다 */
    static List<DashboardSummaryInfo.NameCount> capTop(List<DashboardSummaryInfo.NameCount> rows) {
        if (rows.size() <= TOP_CATEGORY_COUNT + 1) {
            return rows;
        }
        List<DashboardSummaryInfo.NameCount> result = new ArrayList<>(rows.subList(0, TOP_CATEGORY_COUNT));
        long otherSum = rows.subList(TOP_CATEGORY_COUNT, rows.size()).stream()
                .mapToLong(DashboardSummaryInfo.NameCount::getCount)
                .sum();
        result.add(new DashboardSummaryInfo.NameCount(OTHER_LABEL, otherSum));
        return result;
    }

    private List<DashboardSummaryInfo.NameCount> loadStatusDistribution() {
        return tangibleAssetReader.countGroupByLifeStatus().stream()
                .map(row -> new DashboardSummaryInfo.NameCount(
                        ((TangibleAsset.LifeStatus) row[0]).getDescription(), ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }

    List<DashboardSummaryInfo.MonthlyCost> loadMonthlyCostTrend(LocalDate today) {
        LocalDate currentMonth = today.withDayOfMonth(1);
        LocalDate fromMonth = currentMonth.minusMonths(TREND_MONTHS - 1L);

        Map<String, Map<ExpenseRecord.AmountType, BigDecimal>> grouped = new HashMap<>();
        for (Object[] row : expenseRecordReader.sumGroupedFrom(fromMonth)) {
            ExpenseRecord.SrcType srcType = (ExpenseRecord.SrcType) row[0];
            LocalDate month = (LocalDate) row[1];
            ExpenseRecord.AmountType amountType = (ExpenseRecord.AmountType) row[2];
            BigDecimal sum = (BigDecimal) row[3];
            grouped.computeIfAbsent(trendKey(srcType, month), k -> new HashMap<>()).put(amountType, sum);
        }

        List<DashboardSummaryInfo.MonthlyCost> result = new ArrayList<>();
        for (long i = TREND_MONTHS - 1; i >= 0; i--) {
            LocalDate month = currentMonth.minusMonths(i);
            BigDecimal rental = pickAmount(grouped.get(trendKey(ExpenseRecord.SrcType.RENTAL, month)));
            BigDecimal license = pickAmount(grouped.get(trendKey(ExpenseRecord.SrcType.LICENSE, month)));
            result.add(new DashboardSummaryInfo.MonthlyCost(month, rental, license));
        }
        return result;
    }

    private static String trendKey(ExpenseRecord.SrcType srcType, LocalDate month) {
        return srcType.name() + "|" + month;
    }

    /** Q-53: 같은 달에 실제액이 입력돼 있으면 실제액을, 없으면 예상액을 쓴다 */
    static BigDecimal pickAmount(Map<ExpenseRecord.AmountType, BigDecimal> byType) {
        if (byType == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal actual = byType.get(ExpenseRecord.AmountType.ACTUAL);
        if (actual != null) {
            return actual;
        }
        BigDecimal expected = byType.get(ExpenseRecord.AmountType.EXPECTED);
        return expected != null ? expected : BigDecimal.ZERO;
    }

    /**
     * 만료 예정 - 라이선스는 수량 기반이라 만료일 개념이 없어(스키마 확인됨) 무형자산만 대상으로
     * 한다(설계문서는 두 소스를 합치는 것을 전제했으나, ops 문서에 스코프 축소로 기록한다).
     */
    private DashboardSummaryInfo.ExpiringSoon loadExpiringSoon(LocalDate today) {
        long within7 = intangibleAssetReader.findAll(null, today.plusDays(7), PageRequest.of(0, 1)).getTotalElements();
        long within30 = intangibleAssetReader.findAll(null, today.plusDays(30), PageRequest.of(0, 1)).getTotalElements();
        long within90 = intangibleAssetReader.findAll(null, today.plusDays(90), PageRequest.of(0, 1)).getTotalElements();

        List<DashboardSummaryInfo.ExpiringSoon.Item> items = intangibleAssetReader
                .findAll(null, today.plusDays(90), PageRequest.of(0, 5, Sort.by("expiryDate").ascending()))
                .getContent().stream()
                .map(asset -> toExpiringSoonItem(asset, today))
                .collect(Collectors.toList());

        return new DashboardSummaryInfo.ExpiringSoon(within7, within30, within90, items);
    }

    private static DashboardSummaryInfo.ExpiringSoon.Item toExpiringSoonItem(IntangibleAsset asset, LocalDate today) {
        return new DashboardSummaryInfo.ExpiringSoon.Item(
                asset.getId(), asset.getName(), asset.getExpiryDate(), ChronoUnit.DAYS.between(today, asset.getExpiryDate()));
    }

    DashboardSummaryInfo.TicketStatus loadTicketStatus(LocalDate today) {
        long waiting = ticketReader.countByStatusIn(EnumSet.of(Ticket.Status.WAITING, Ticket.Status.RECEIVED));
        long inProgress = ticketReader.countByStatusIn(EnumSet.of(Ticket.Status.IN_PROGRESS));
        long overdue = ticketReader.countOverdue(today);
        long doneRecently = ticketReader.countCompletedSince(OffsetDateTime.now().minusDays(RECENT_DONE_WINDOW_DAYS));
        return new DashboardSummaryInfo.TicketStatus(waiting, inProgress, overdue, doneRecently);
    }

    private List<DashboardSummaryInfo.InventoryProgress> loadInventoryProgress() {
        return inventoryReader.findAllInProgress().stream()
                .map(this::toInventoryProgress)
                .collect(Collectors.toList());
    }

    private DashboardSummaryInfo.InventoryProgress toInventoryProgress(Inventory inventory) {
        InventoryProgressInfo progress = inventoryService.loadProgress(inventory.getId());
        return new DashboardSummaryInfo.InventoryProgress(
                inventory.getId(), inventory.getTitle(),
                progress.getConfirmedCount(), progress.getTotalCount(), progress.getParticipants().size());
    }

    /** Q-47: 라이선스별 배정건수(활성)가 구매수량 합계를 넘는지 비교 - 구매내역이 없는 라이선스도 대상에 포함한다 */
    DashboardSummaryInfo.LicenseConsistency loadLicenseConsistency() {
        Map<UUID, Long> purchasedByLicense = toLicenseCountMap(licensePurchaseRecordReader.sumQuantityGroupByLicense());
        Map<UUID, Long> assignedByLicense = toLicenseCountMap(licenseAssignedUserReader.countActiveGroupByLicense());

        Set<UUID> allLicenseIds = new HashSet<>();
        allLicenseIds.addAll(purchasedByLicense.keySet());
        allLicenseIds.addAll(assignedByLicense.keySet());

        long overCapacityCount = allLicenseIds.stream()
                .filter(id -> assignedByLicense.getOrDefault(id, 0L) > purchasedByLicense.getOrDefault(id, 0L))
                .count();

        return new DashboardSummaryInfo.LicenseConsistency(overCapacityCount, licenseAssignedUserReader.countUnlinkedActive());
    }

    private static Map<UUID, Long> toLicenseCountMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                row -> (UUID) row[0],
                row -> ((Number) row[1]).longValue()));
    }

    @Override
    public List<WidgetConfigInfo> loadWidgetConfig() {
        Map<DashboardWidgetKey, DashboardWidgetConfig> saved = dashboardWidgetConfigReader
                .findAllByMemberId(loginUserContext.getUserId()).stream()
                .collect(Collectors.toMap(DashboardWidgetConfig::getWidgetKey, c -> c));

        List<WidgetConfigInfo> result = new ArrayList<>();
        int defaultOrder = 0;
        for (DashboardWidgetKey key : DashboardWidgetKey.values()) {
            DashboardWidgetConfig config = saved.get(key);
            result.add(config != null
                    ? new WidgetConfigInfo(key, config.getVisible(), config.getSortOrder())
                    : new WidgetConfigInfo(key, true, defaultOrder));
            defaultOrder++;
        }
        result.sort(Comparator.comparingInt(WidgetConfigInfo::getSortOrder));
        return result;
    }

    @Override
    public void saveWidgetConfig(List<WidgetConfigCommand> commands) {
        UUID memberId = loginUserContext.getUserId();
        List<DashboardWidgetConfig> configs = commands.stream()
                .map(command -> DashboardWidgetConfig.builder()
                        .memberId(memberId)
                        .widgetKey(command.getWidgetKey())
                        .visible(command.getVisible())
                        .sortOrder(command.getSortOrder())
                        .build())
                .collect(Collectors.toList());
        dashboardWidgetConfigStore.replaceAll(memberId, configs);
    }
}
