package com.winitech.smartAsset.domain.license;

import java.util.List;
import java.util.UUID;

public interface LicenseIncludedSoftwareReader {

    LicenseIncludedSoftware findById(UUID licenseIncludedSoftwareId);

    List<LicenseIncludedSoftware> findAllByLicenseId(UUID licenseId);
}
