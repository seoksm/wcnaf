package com.winitech.smartAsset.domain.license;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LicenseReader {

    License findById(UUID licenseId);

    Page<License> findAll(String keyword, Pageable pageable);
}
