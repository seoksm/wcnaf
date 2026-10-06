package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.License;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface LicenseRepository extends JpaRepository<License, UUID> {

    @Query(
            value = "select l from License l where l.status = 'ENABLE' and (:keyword IS NULL OR l.name LIKE %:keyword%)",
            countQuery = "select count(l) from License l where l.status = 'ENABLE' and (:keyword IS NULL OR l.name LIKE %:keyword%)"
    )
    Page<License> findAllByContainsKeyword(String keyword, Pageable pageable);
}
