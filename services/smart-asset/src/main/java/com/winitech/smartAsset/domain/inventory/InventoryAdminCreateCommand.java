package com.winitech.smartAsset.domain.inventory;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** S-302 전수조사 생성(관리자형) 요청 - 공용·미배정 자산 대상, 특정 자산이 아니라 검수자 풀 전체가 검수(Q-32) */
@Getter
@Builder
public class InventoryAdminCreateCommand {
    private String title;
    private Boolean approvalRequired;
    private Boolean allowNewAssetRegistration;
    private Inventory.RecurrenceRule recurrenceRule;
    private List<UUID> inspectorMemberIds;
}
