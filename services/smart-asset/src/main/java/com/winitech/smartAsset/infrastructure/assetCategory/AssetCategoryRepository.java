package com.winitech.smartAsset.infrastructure.assetCategory;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, UUID> {

    boolean existsByCategoryCodeAndStatus(String categoryCode, AssetCategory.Status status);

    @Query("select c from AssetCategory c " +
            "where c.status = 'ENABLE' " +
            "and (:keyword IS NULL OR c.categoryName LIKE %:keyword%) " +
            "order by c.sortSeq asc, c.categoryName asc")
    List<AssetCategory> findAllByContainsKeyword(String keyword);
}
