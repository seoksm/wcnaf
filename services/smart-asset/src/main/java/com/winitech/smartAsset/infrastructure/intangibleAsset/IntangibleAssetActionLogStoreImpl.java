package com.winitech.smartAsset.infrastructure.intangibleAsset;

import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetActionLog;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetActionLogStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class IntangibleAssetActionLogStoreImpl implements IntangibleAssetActionLogStore {

    private final IntangibleAssetActionLogRepository intangibleAssetActionLogRepository;

    @Override
    public IntangibleAssetActionLog store(IntangibleAssetActionLog log) {
        return intangibleAssetActionLogRepository.save(log);
    }
}
