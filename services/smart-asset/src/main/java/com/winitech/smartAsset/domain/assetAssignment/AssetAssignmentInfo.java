package com.winitech.smartAsset.domain.assetAssignment;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class AssetAssignmentInfo {

    private final UUID assetAssignmentId;
    private final UUID memberId;
    private final String assignType;
    private final OffsetDateTime assignedAt;
    private final OffsetDateTime releasedAt;
    private final UUID assignedBy;

    public AssetAssignmentInfo(AssetAssignment assetAssignment) {
        this.assetAssignmentId = assetAssignment.getId();
        this.memberId = assetAssignment.getMemberId();
        this.assignType = assetAssignment.getAssignType();
        this.assignedAt = assetAssignment.getAssignedAt();
        this.releasedAt = assetAssignment.getReleasedAt();
        this.assignedBy = assetAssignment.getAssignedBy();
    }
}
