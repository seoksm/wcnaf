package com.winitech.smartAsset.domain.inventory;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * S-306 "템플릿 복제" - 직전(또는 임의의) 조사의 생성 설정을 그대로 읽어 새 생성 다이얼로그를
 * 미리 채워준다(§4 "반기마다 대상 범위·옵션을 다시 고르는 건 낭비다"). 대상 자산 자체는 항상
 * 시행 시점에 새로 산정하므로(Q-32 스냅샷 원칙) 여기서는 대상 목록이 아니라 "옵션"만 복제한다.
 */
@Getter
public class InventoryCloneTemplateInfo {

    private final Inventory.InventoryType inventoryType;
    private final String title;
    private final Boolean approvalRequired;
    private final Boolean allowNewAssetRegistration;
    private final Inventory.RecurrenceRule recurrenceRule;
    /** 임직원형에서만 값이 있다 */
    private final List<UUID> excludedMemberIds;
    /** 관리자형에서만 값이 있다 */
    private final List<UUID> inspectorMemberIds;

    public InventoryCloneTemplateInfo(Inventory source, List<UUID> excludedMemberIds, List<UUID> inspectorMemberIds) {
        this.inventoryType = source.getInventoryType();
        this.title = source.getTitle();
        this.approvalRequired = source.getApprovalRequired();
        this.allowNewAssetRegistration = source.getAllowNewAssetRegistration();
        this.recurrenceRule = source.getRecurrenceRule();
        this.excludedMemberIds = excludedMemberIds;
        this.inspectorMemberIds = inspectorMemberIds;
    }
}
