package com.winitech.smartAsset.domain.license;

import java.util.UUID;

public interface LicenseIncludedSoftwareStore {

    LicenseIncludedSoftware store(LicenseIncludedSoftware includedSoftware);

    void remove(UUID licenseIncludedSoftwareId);
}
