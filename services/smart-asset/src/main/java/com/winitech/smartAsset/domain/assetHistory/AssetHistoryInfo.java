package com.winitech.smartAsset.domain.assetHistory;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class AssetHistoryInfo {

    private final UUID assetHistoryId;
    private final UUID tangibleAssetId;
    private final String assetCode;
    private final String assetName;
    private final String historyType;
    private final String changedFields;
    private final String snapshot;
    private final UUID batchId;
    private final UUID createdBy;
    private final OffsetDateTime createAt;

    public AssetHistoryInfo(AssetHistory assetHistory) {
        this.assetHistoryId = assetHistory.getId();
        this.tangibleAssetId = assetHistory.getTangibleAsset().getId();
        this.assetCode = assetHistory.getTangibleAsset().getAssetCode();
        this.assetName = assetHistory.getTangibleAsset().getAssetName();
        this.historyType = assetHistory.getHistoryType().name();
        this.changedFields = assetHistory.getChangedFields();
        this.snapshot = assetHistory.getSnapshot();
        this.batchId = assetHistory.getBatchId();
        this.createdBy = assetHistory.getCreatedBy();
        this.createAt = assetHistory.getCreateAt();
    }
}
