package com.winitech.smartAsset.domain.intangibleAsset;

import java.util.List;
import java.util.UUID;

public interface IntangibleAssetActionLogReader {

    List<IntangibleAssetActionLog> findAllByIntangibleAssetId(UUID intangibleAssetId);
}
