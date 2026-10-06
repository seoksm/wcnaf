package com.winitech.smartAsset.infrastructure.assetLocation;

import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AssetLocationRepository extends JpaRepository<AssetLocation, UUID> {

    @Query("select l from AssetLocation l " +
            "where l.status = 'ENABLE' " +
            "and (:keyword IS NULL OR l.locationName LIKE %:keyword%) " +
            "order by l.sortSeq asc, l.locationName asc")
    List<AssetLocation> findAllByContainsKeyword(String keyword);
}
