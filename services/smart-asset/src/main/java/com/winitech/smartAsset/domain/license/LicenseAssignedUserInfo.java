package com.winitech.smartAsset.domain.license;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class LicenseAssignedUserInfo {

    private final UUID licenseAssignedUserId;
    private final UUID licenseId;
    private final String licenseName;
    private final UUID memberId;
    private final UUID licensePurchaseRecordId;
    private final boolean unlinkedPurchase;
    private final OffsetDateTime assignedAt;
    private final UUID assignedBy;
    private final OffsetDateTime releasedAt;
    private final boolean active;
    private final boolean releaseRequestedYn;
    private final OffsetDateTime releaseRequestedAt;

    public LicenseAssignedUserInfo(LicenseAssignedUser assignedUser) {
        this.licenseAssignedUserId = assignedUser.getId();
        this.licenseId = assignedUser.getLicense().getId();
        this.licenseName = assignedUser.getLicense().getName();
        this.memberId = assignedUser.getMemberId();
        this.licensePurchaseRecordId = assignedUser.getLicensePurchaseRecord() != null
                ? assignedUser.getLicensePurchaseRecord().getId() : null;
        this.unlinkedPurchase = assignedUser.getLicensePurchaseRecord() == null;
        this.assignedAt = assignedUser.getAssignedAt();
        this.assignedBy = assignedUser.getAssignedBy();
        this.releasedAt = assignedUser.getReleasedAt();
        this.active = assignedUser.isActive();
        this.releaseRequestedYn = Boolean.TRUE.equals(assignedUser.getReleaseRequestedYn());
        this.releaseRequestedAt = assignedUser.getReleaseRequestedAt();
    }
}
