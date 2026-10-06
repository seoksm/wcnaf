package com.winitech.smartAsset.domain.intangibleAsset;

import java.util.UUID;

public interface IntangibleAssetStore {

    UUID store(IntangibleAsset intangibleAsset);

    void modify(IntangibleAsset intangibleAsset, IntangibleAssetCommand.UpdateCommand updateCommand);

    void delete(IntangibleAsset intangibleAsset);
}
