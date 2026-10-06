package com.winitech.smartAsset.domain.inventory;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** S-303(자산별/이상보고 탭)·S-304(검수 승인/반려)·S-308(종결 처리) 목록의 행 1개 */
@Getter
public class InventoryResultRowInfo {

    private final UUID inventoryTargetId;
    private final UUID inventoryResultId;
    private final UUID tangibleAssetId;
    private final String assetCode;
    private final String assetName;
    private final String categoryName;
    private final UUID memberId;
    private final InventoryResult.Status status;
    private final InventoryResult.AnomalyType anomalyType;
    private final String note;
    private final UUID reviewedBy;
    private final OffsetDateTime reviewedAt;
    private final String rejectionReason;
    private final InventoryResult.ClosureAction closureAction;
    private final InventoryResult.ClosureReasonCode closureReasonCode;
    private final String closureNote;
    /** S-308 "장부가 노출" - 분실 처리가 곧 처분손실이라는 것을 관리자가 인지하도록 */
    private final BigDecimal bookValue;

    public InventoryResultRowInfo(InventoryResult result, BigDecimal bookValue) {
        InventoryTarget target = result.getInventoryTarget();
        TangibleAsset asset = target.getTangibleAsset();

        this.inventoryTargetId = target.getId();
        this.inventoryResultId = result.getId();
        this.tangibleAssetId = asset.getId();
        this.assetCode = asset.getAssetCode();
        this.assetName = asset.getAssetName();
        this.categoryName = asset.getCategory() != null ? asset.getCategory().getCategoryName() : null;
        this.memberId = target.getMemberId();
        this.status = result.getStatus();
        this.anomalyType = result.getAnomalyType();
        this.note = result.getNote();
        this.reviewedBy = result.getReviewedBy();
        this.reviewedAt = result.getReviewedAt();
        this.rejectionReason = result.getRejectionReason();
        this.closureAction = result.getClosureAction();
        this.closureReasonCode = result.getClosureReasonCode();
        this.closureNote = result.getClosureNote();
        this.bookValue = bookValue;
    }
}
