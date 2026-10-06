package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.LicenseIncludedSoftware;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LicenseIncludedSoftwareRepository extends JpaRepository<LicenseIncludedSoftware, UUID> {

    List<LicenseIncludedSoftware> findAllByLicense_Id(UUID licenseId);
}
