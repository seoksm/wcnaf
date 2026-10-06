package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.LicenseIncludedSoftware;
import com.winitech.smartAsset.domain.license.LicenseIncludedSoftwareStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LicenseIncludedSoftwareStoreImpl implements LicenseIncludedSoftwareStore {

    private final LicenseIncludedSoftwareRepository licenseIncludedSoftwareRepository;

    @Override
    public LicenseIncludedSoftware store(LicenseIncludedSoftware includedSoftware) {
        return licenseIncludedSoftwareRepository.save(includedSoftware);
    }

    @Override
    public void remove(UUID licenseIncludedSoftwareId) {
        licenseIncludedSoftwareRepository.deleteById(licenseIncludedSoftwareId);
    }
}
