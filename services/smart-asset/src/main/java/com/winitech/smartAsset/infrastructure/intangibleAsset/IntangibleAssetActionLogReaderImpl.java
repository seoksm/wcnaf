package com.winitech.smartAsset.infrastructure.intangibleAsset;

import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetActionLog;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetActionLogReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class IntangibleAssetActionLogReaderImpl implements IntangibleAssetActionLogReader {

    private final IntangibleAssetActionLogRepository intangibleAssetActionLogRepository;

    @Override
    public List<IntangibleAssetActionLog> findAllByIntangibleAssetId(UUID intangibleAssetId) {
        return intangibleAssetActionLogRepository.findAllByIntangibleAsset_IdOrderByActedAtDesc(intangibleAssetId);
    }
}
