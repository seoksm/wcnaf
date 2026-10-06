package com.winitech.smartAsset.infrastructure.vendor;

import com.winitech.smartAsset.domain.vendor.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {

    @Query("select v from Vendor v where v.status = 'ENABLE' " +
            "and (:keyword IS NULL OR v.name LIKE %:keyword%) order by v.name asc")
    List<Vendor> findAllByContainsKeyword(String keyword);
}
