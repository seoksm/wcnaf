package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.LicensePurchaseRecord;
import com.winitech.smartAsset.domain.license.LicensePurchaseRecordReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LicensePurchaseRecordReaderImpl implements LicensePurchaseRecordReader {

    private final LicensePurchaseRecordRepository licensePurchaseRecordRepository;

    @Override
    public LicensePurchaseRecord findById(UUID licensePurchaseRecordId) {
        return licensePurchaseRecordRepository.findById(licensePurchaseRecordId).orElseThrow();
    }

    @Override
    public List<LicensePurchaseRecord> findAllByLicenseId(UUID licenseId) {
        return licensePurchaseRecordRepository.findAllByLicense_IdOrderByPurchaseDateDesc(licenseId);
    }

    @Override
    public int sumQuantityByLicenseId(UUID licenseId) {
        return licensePurchaseRecordRepository.sumQuantityByLicenseId(licenseId);
    }

    @Override
    public List<Object[]> sumQuantityGroupByLicense() {
        return licensePurchaseRecordRepository.sumQuantityGroupByLicense();
    }
}
