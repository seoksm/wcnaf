package com.winitech.smartAsset.infrastructure.intangibleAsset;

import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IntangibleAssetActionLogRepository extends JpaRepository<IntangibleAssetActionLog, UUID> {

    List<IntangibleAssetActionLog> findAllByIntangibleAsset_IdOrderByActedAtDesc(UUID intangibleAssetId);
}
