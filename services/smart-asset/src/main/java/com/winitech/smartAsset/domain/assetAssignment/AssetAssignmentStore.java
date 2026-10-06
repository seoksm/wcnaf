package com.winitech.smartAsset.domain.assetAssignment;

public interface AssetAssignmentStore {

    AssetAssignment store(AssetAssignment assetAssignment);

    void release(AssetAssignment assetAssignment);
}
