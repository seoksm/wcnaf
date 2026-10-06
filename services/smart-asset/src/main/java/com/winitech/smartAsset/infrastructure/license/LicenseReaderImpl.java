package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.License;
import com.winitech.smartAsset.domain.license.LicenseReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LicenseReaderImpl implements LicenseReader {

    private final LicenseRepository licenseRepository;

    @Override
    public License findById(UUID licenseId) {
        return licenseRepository.findById(licenseId).orElseThrow();
    }

    @Override
    public Page<License> findAll(String keyword, Pageable pageable) {
        return licenseRepository.findAllByContainsKeyword(keyword, pageable);
    }
}
