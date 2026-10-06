package com.winitech.smartAsset.domain.intangibleAsset;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class IntangibleAssetInfo {

    private final UUID intangibleAssetId;
    private final IntangibleAsset.Type intangibleType;
    private final String name;
    private final String issuer;
    private final LocalDate registeredDate;
    private final LocalDate expiryDate;
    private final UUID ownerMemberId;
    private final String alertDays;
    private final String memo;
    private final IntangibleAsset.Status status;
    private final long daysUntilExpiry;
    private final boolean expired;
    private final boolean nearExpiry;

    public IntangibleAssetInfo(IntangibleAsset asset) {
        this.intangibleAssetId = asset.getId();
        this.intangibleType = asset.getIntangibleType();
        this.name = asset.getName();
        this.issuer = asset.getIssuer();
        this.registeredDate = asset.getRegisteredDate();
        this.expiryDate = asset.getExpiryDate();
        this.ownerMemberId = asset.getOwnerMemberId();
        this.alertDays = asset.getAlertDays();
        this.memo = asset.getMemo();
        this.status = asset.getStatus();
        this.daysUntilExpiry = asset.daysUntilExpiry();
        this.expired = asset.isExpired();
        this.nearExpiry = asset.isNearExpiry();
    }
}
