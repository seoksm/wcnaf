package com.winitech.smartAsset.domain.inventory;

import lombok.Getter;

import java.util.UUID;
import java.util.List;

/** S-301/302 대상 미리보기 - 시행 전 마지막 확인 지점(시행 후 대상은 변경 불가, I1) */
@Getter
public class InventoryPreviewInfo {

    private final long targetAssetCount;
    private final long participantCount;
    /** S-306 이월 추적 - 직전(동일 유형) 종료된 조사에서 차기이월로 넘어온 자산 ID들 */
    private final List<UUID> carriedOverTangibleAssetIds;

    public InventoryPreviewInfo(long targetAssetCount, long participantCount, List<UUID> carriedOverTangibleAssetIds) {
        this.targetAssetCount = targetAssetCount;
        this.participantCount = participantCount;
        this.carriedOverTangibleAssetIds = carriedOverTangibleAssetIds;
    }
}
