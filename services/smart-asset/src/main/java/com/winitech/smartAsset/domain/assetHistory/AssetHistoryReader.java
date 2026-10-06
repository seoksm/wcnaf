package com.winitech.smartAsset.domain.assetHistory;

import java.util.List;
import java.util.UUID;

public interface AssetHistoryReader {

    List<AssetHistory> findAllByTangibleAssetId(UUID tangibleAssetId);

    /** S-221 전체 활동 로그 - 0부터 시작하는 page 번호, 페이지당 200건 고정 */
    List<AssetHistory> findRecent(AssetHistoryFilter filter, int page);
}
