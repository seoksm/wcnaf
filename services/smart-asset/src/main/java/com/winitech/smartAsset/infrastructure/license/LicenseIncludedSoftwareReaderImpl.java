package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.LicenseIncludedSoftware;
import com.winitech.smartAsset.domain.license.LicenseIncludedSoftwareReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LicenseIncludedSoftwareReaderImpl implements LicenseIncludedSoftwareReader {

    private final LicenseIncludedSoftwareRepository licenseIncludedSoftwareRepository;

    @Override
    public LicenseIncludedSoftware findById(UUID licenseIncludedSoftwareId) {
        return licenseIncludedSoftwareRepository.findById(licenseIncludedSoftwareId).orElseThrow();
    }

    @Override
    public List<LicenseIncludedSoftware> findAllByLicenseId(UUID licenseId) {
        return licenseIncludedSoftwareRepository.findAllByLicense_Id(licenseId);
    }
}
