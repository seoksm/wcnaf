package com.winitech.smartAsset.domain.dashboard;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * S-700 대시보드 응답 - 위젯 10종을 위한 데이터를 한 번에 묶어 내려준다. 표시여부·순서는 여기
 * 담지 않는다(그건 S-701 설정 API의 몫) - 이 응답은 항상 전체 위젯 데이터를 계산해서 돌려주고,
 * 화면에서 설정에 따라 숨기거나 재배열한다(캐시를 두지 않기로 한 결정과 맞물려, 매 요청 최신값).
 */
@Getter
public class DashboardSummaryInfo {

    private final OffsetDateTime aggregatedAt;
    private final AssetSummary assetSummary;
    private final List<NameCount> categoryDistribution;
    private final List<NameCount> locationDistribution;
    private final List<NameCount> statusDistribution;
    private final List<MonthlyCost> monthlyCostTrend;
    private final ExpiringSoon expiringSoon;
    private final TicketStatus ticketStatus;
    private final LoanStatus loanStatus;
    private final List<InventoryProgress> inventoryProgress;
    private final LicenseConsistency licenseConsistency;

    public DashboardSummaryInfo(OffsetDateTime aggregatedAt, AssetSummary assetSummary,
                                 List<NameCount> categoryDistribution, List<NameCount> locationDistribution,
                                 List<NameCount> statusDistribution, List<MonthlyCost> monthlyCostTrend,
                                 ExpiringSoon expiringSoon, TicketStatus ticketStatus, LoanStatus loanStatus,
                                 List<InventoryProgress> inventoryProgress, LicenseConsistency licenseConsistency) {
        this.aggregatedAt = aggregatedAt;
        this.assetSummary = assetSummary;
        this.categoryDistribution = categoryDistribution;
        this.locationDistribution = locationDistribution;
        this.statusDistribution = statusDistribution;
        this.monthlyCostTrend = monthlyCostTrend;
        this.expiringSoon = expiringSoon;
        this.ticketStatus = ticketStatus;
        this.loanStatus = loanStatus;
        this.inventoryProgress = inventoryProgress;
        this.licenseConsistency = licenseConsistency;
    }

    /** 자산 총계 KPI - D7에 따라 미확정 기간은 실시간 계산, 확정된 기간은 스냅샷을 그대로 사용(DepreciationService.loadStatus 재사용). */
    @Getter
    public static class AssetSummary {
        private final long assetCount;
        private final java.math.BigDecimal totalAcquisitionAmount;
        private final java.math.BigDecimal totalAccumulatedDepreciation;
        private final java.math.BigDecimal totalBookValue;
        private final boolean confirmed;
        private final OffsetDateTime basedOn;

        public AssetSummary(long assetCount, java.math.BigDecimal totalAcquisitionAmount,
                             java.math.BigDecimal totalAccumulatedDepreciation, java.math.BigDecimal totalBookValue,
                             boolean confirmed, OffsetDateTime basedOn) {
            this.assetCount = assetCount;
            this.totalAcquisitionAmount = totalAcquisitionAmount;
            this.totalAccumulatedDepreciation = totalAccumulatedDepreciation;
            this.totalBookValue = totalBookValue;
            this.confirmed = confirmed;
            this.basedOn = basedOn;
        }
    }

    /** 종류별/위치별/상태별 현황 공용 행 - V4(7범주 상한)는 이 리스트를 만드는 쪽(Service)에서 적용한다. */
    @Getter
    public static class NameCount {
        private final String name;
        private final long count;

        public NameCount(String name, long count) {
            this.name = name;
            this.count = count;
        }
    }

    /** 월별 비용추이 한 달치 - Q-52/53: src_type(RENTAL/LICENSE)별로 실제액이 있으면 실제액, 없으면 예상액. */
    @Getter
    public static class MonthlyCost {
        private final java.time.LocalDate month;
        private final java.math.BigDecimal rentalAmount;
        private final java.math.BigDecimal licenseAmount;

        public MonthlyCost(java.time.LocalDate month, java.math.BigDecimal rentalAmount, java.math.BigDecimal licenseAmount) {
            this.month = month;
            this.rentalAmount = rentalAmount;
            this.licenseAmount = licenseAmount;
        }
    }

    /** 만료 예정 - 라이선스는 만료 개념이 없어(스키마상 수량 기반) 무형자산만 대상(스코프 축소, ops 문서에 기록). */
    @Getter
    public static class ExpiringSoon {
        private final long within7Days;
        private final long within30Days;
        private final long within90Days;
        private final List<Item> items;

        public ExpiringSoon(long within7Days, long within30Days, long within90Days, List<Item> items) {
            this.within7Days = within7Days;
            this.within30Days = within30Days;
            this.within90Days = within90Days;
            this.items = items;
        }

        @Getter
        public static class Item {
            private final java.util.UUID intangibleAssetId;
            private final String name;
            private final java.time.LocalDate expiryDate;
            private final long daysLeft;

            public Item(java.util.UUID intangibleAssetId, String name, java.time.LocalDate expiryDate, long daysLeft) {
                this.intangibleAssetId = intangibleAssetId;
                this.name = name;
                this.expiryDate = expiryDate;
                this.daysLeft = daysLeft;
            }
        }
    }

    /** 티켓 현황 KPI 4칸 - Q-50(미배정 경고)·Q-51(SLA 초과)을 그대로 반영. */
    @Getter
    public static class TicketStatus {
        private final long waitingCount;
        private final long inProgressCount;
        private final long overdueCount;
        private final long doneThisWeekCount;

        public TicketStatus(long waitingCount, long inProgressCount, long overdueCount, long doneThisWeekCount) {
            this.waitingCount = waitingCount;
            this.inProgressCount = inProgressCount;
            this.overdueCount = overdueCount;
            this.doneThisWeekCount = doneThisWeekCount;
        }
    }

    /** 대여 현황 - 대여 프로세스 OFF 여부는 프론트가 process-config로 판단해 렌더링 여부를 정한다(§1 프로세스 의존). */
    @Getter
    public static class LoanStatus {
        private final long activeCount;
        private final long overdueCount;

        public LoanStatus(long activeCount, long overdueCount) {
            this.activeCount = activeCount;
            this.overdueCount = overdueCount;
        }
    }

    /** 전수조사 진행률 - 진행 중(IN_PROGRESS)인 조사 전체(0건일 수도, 2건 이상일 수도 있음). */
    @Getter
    public static class InventoryProgress {
        private final java.util.UUID inventoryId;
        private final String title;
        private final long confirmedCount;
        private final long totalCount;
        private final long participantCount;

        public InventoryProgress(java.util.UUID inventoryId, String title, long confirmedCount, long totalCount, long participantCount) {
            this.inventoryId = inventoryId;
            this.title = title;
            this.confirmedCount = confirmedCount;
            this.totalCount = totalCount;
            this.participantCount = participantCount;
        }
    }

    /** 라이선스 정합성 - Q-47(초과는 경고만)·미연결 배정(Q-47 "선배정 후구매"의 실제 원인) 둘 다 노출. */
    @Getter
    public static class LicenseConsistency {
        private final long overCapacityLicenseCount;
        private final long unlinkedAssignmentCount;

        public LicenseConsistency(long overCapacityLicenseCount, long unlinkedAssignmentCount) {
            this.overCapacityLicenseCount = overCapacityLicenseCount;
            this.unlinkedAssignmentCount = unlinkedAssignmentCount;
        }
    }
}
