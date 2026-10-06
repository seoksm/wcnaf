package com.winitech.smartAsset.domain.inventory;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * S-305 리포트 - 화면(진행 중이든 종료든 조회 가능)과 엑셀 다운로드(참고용)가 공유하는 데이터.
 * §4 설계의 핵심 섹션인 "미확인 처리 내역"(closureBreakdown)과 분실 목록(lostItems)이 감사 대응의
 * 실제 답변 근거다. PDF는 이 서비스에 PDF 생성 인프라가 없어 미구현(운영 문서에 한계로 기록).
 */
@Getter
public class InventoryReportInfo {

    private final UUID inventoryId;
    private final String title;
    private final Inventory.InventoryType inventoryType;
    private final Inventory.Status status;
    private final OffsetDateTime createAt;
    private final OffsetDateTime closedAt;
    private final Inventory.RecurrenceRule recurrenceRule;

    private final long totalCount;
    private final long confirmedCount;
    private final long pendingApprovalCount;
    private final long anomalyCount;
    private final long unconfirmedCount;

    /** 임직원형(MEMBER)에서만 값이 있다(관리자형은 특정 개인에게 대상이 걸려 있지 않다, Q-32) */
    private final List<InventoryProgressInfo.ParticipantSummary> participants;
    /** "69건이 미확인이었고 그중 12건은 휴직자 이월, 2건은 분실 처리"를 만드는 핵심 섹션(§4) */
    private final List<ClosureBreakdownItem> closureBreakdown;
    /** 분실 처리된 자산 - 장부가를 같이 노출해 처분손실 규모를 바로 알 수 있게 한다 */
    private final List<LostItem> lostItems;
    /** 확인은 됐지만 대장과 다른 경우(I4)의 유형별 분해 */
    private final List<AnomalyBreakdownItem> anomalyBreakdown;

    public InventoryReportInfo(Inventory inventory, long totalCount, long confirmedCount, long pendingApprovalCount,
                                long anomalyCount, long unconfirmedCount,
                                List<InventoryProgressInfo.ParticipantSummary> participants,
                                List<ClosureBreakdownItem> closureBreakdown, List<LostItem> lostItems,
                                List<AnomalyBreakdownItem> anomalyBreakdown) {
        this.inventoryId = inventory.getId();
        this.title = inventory.getTitle();
        this.inventoryType = inventory.getInventoryType();
        this.status = inventory.getStatus();
        this.createAt = inventory.getCreateAt();
        this.closedAt = inventory.getClosedAt();
        this.recurrenceRule = inventory.getRecurrenceRule();
        this.totalCount = totalCount;
        this.confirmedCount = confirmedCount;
        this.pendingApprovalCount = pendingApprovalCount;
        this.anomalyCount = anomalyCount;
        this.unconfirmedCount = unconfirmedCount;
        this.participants = participants;
        this.closureBreakdown = closureBreakdown;
        this.lostItems = lostItems;
        this.anomalyBreakdown = anomalyBreakdown;
    }

    @Getter
    public static class ClosureBreakdownItem {
        private final InventoryResult.ClosureAction closureAction;
        private final InventoryResult.ClosureReasonCode closureReasonCode;
        private final long count;

        public ClosureBreakdownItem(InventoryResult.ClosureAction closureAction,
                                     InventoryResult.ClosureReasonCode closureReasonCode, long count) {
            this.closureAction = closureAction;
            this.closureReasonCode = closureReasonCode;
            this.count = count;
        }
    }

    @Getter
    public static class LostItem {
        private final UUID tangibleAssetId;
        private final String assetCode;
        private final String assetName;
        private final BigDecimal bookValue;
        private final String closureNote;

        public LostItem(UUID tangibleAssetId, String assetCode, String assetName, BigDecimal bookValue, String closureNote) {
            this.tangibleAssetId = tangibleAssetId;
            this.assetCode = assetCode;
            this.assetName = assetName;
            this.bookValue = bookValue;
            this.closureNote = closureNote;
        }
    }

    @Getter
    public static class AnomalyBreakdownItem {
        private final InventoryResult.AnomalyType anomalyType;
        private final long count;

        public AnomalyBreakdownItem(InventoryResult.AnomalyType anomalyType, long count) {
            this.anomalyType = anomalyType;
            this.count = count;
        }
    }
}
