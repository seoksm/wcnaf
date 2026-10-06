package com.winitech.smartAsset.domain.inventory;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** S-301 전수조사 생성(임직원형) 요청 */
@Getter
@Builder
public class InventoryMemberCreateCommand {
    private String title;
    private Boolean approvalRequired;
    private Boolean allowNewAssetRegistration;
    private Inventory.RecurrenceRule recurrenceRule;
    /** Q-37: 휴직·장기출장 등으로 수동 제외한 임직원 - 자동(상태 기준) 판정은 미구현 */
    private List<UUID> excludedMemberIds;
}
