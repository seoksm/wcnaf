package com.winitech.smartAsset.domain.license;

import lombok.Getter;

import java.util.UUID;

/**
 * 설계문서 "수량 정합성" 요구사항 - 보유(구매합계)·배정(유효 배정건수)·잔여를 항상 함께 노출한다.
 * Q-47: 잔여가 음수(초과배정)여도 막지 않고 경고만 한다 - overAssigned로 표시.
 */
@Getter
public class LicenseInfo {

    private final UUID licenseId;
    private final String name;
    private final String memo;
    private final License.Status status;
    private final int purchasedQuantity;
    private final int assignedQuantity;
    private final int remainingQuantity;
    private final boolean overAssigned;

    public LicenseInfo(License license, int purchasedQuantity, int assignedQuantity) {
        this.licenseId = license.getId();
        this.name = license.getName();
        this.memo = license.getMemo();
        this.status = license.getStatus();
        this.purchasedQuantity = purchasedQuantity;
        this.assignedQuantity = assignedQuantity;
        this.remainingQuantity = purchasedQuantity - assignedQuantity;
        this.overAssigned = this.remainingQuantity < 0;
    }
}
