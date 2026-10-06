package com.winitech.smartAsset.infrastructure.rental;

import com.winitech.smartAsset.domain.rental.RentalAsset;
import com.winitech.smartAsset.domain.rental.RentalReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RentalReaderImpl implements RentalReader {

    private final RentalRepository rentalRepository;

    @Override
    public RentalAsset findById(UUID rentalAssetId) {
        return rentalRepository.findById(rentalAssetId).orElseThrow();
    }

    @Override
    public Page<RentalAsset> findAll(String keyword, Pageable pageable) {
        return rentalRepository.findAllByContainsKeyword(keyword, pageable);
    }
}
