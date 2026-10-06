package com.winitech.smartAsset.infrastructure.rental;

import com.winitech.smartAsset.domain.rental.RentalAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RentalRepository extends JpaRepository<RentalAsset, UUID> {

    @Query(
            value = "select r from RentalAsset r where (:keyword IS NULL OR r.name LIKE %:keyword%)",
            countQuery = "select count(r) from RentalAsset r where (:keyword IS NULL OR r.name LIKE %:keyword%)"
    )
    Page<RentalAsset> findAllByContainsKeyword(String keyword, Pageable pageable);
}
