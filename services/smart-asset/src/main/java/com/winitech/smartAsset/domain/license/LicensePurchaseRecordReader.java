package com.winitech.smartAsset.domain.license;

import java.util.List;
import java.util.UUID;

public interface LicensePurchaseRecordReader {

    LicensePurchaseRecord findById(UUID licensePurchaseRecordId);

    List<LicensePurchaseRecord> findAllByLicenseId(UUID licenseId);

    int sumQuantityByLicenseId(UUID licenseId);

    /** S-700 라이선스 정합성 - [0]=license_id(UUID), [1]=합계(Long) */
    List<Object[]> sumQuantityGroupByLicense();
}
