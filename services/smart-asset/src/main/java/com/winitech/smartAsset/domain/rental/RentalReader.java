package com.winitech.smartAsset.domain.rental;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalReader {

    RentalAsset findById(java.util.UUID rentalAssetId);

    Page<RentalAsset> findAll(String keyword, Pageable pageable);
}
