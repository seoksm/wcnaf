package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.LicensePurchaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LicensePurchaseRecordRepository extends JpaRepository<LicensePurchaseRecord, UUID> {

    List<LicensePurchaseRecord> findAllByLicense_IdOrderByPurchaseDateDesc(UUID licenseId);

    @Query("select coalesce(sum(r.quantity), 0) from LicensePurchaseRecord r where r.license.id = :licenseId")
    int sumQuantityByLicenseId(UUID licenseId);

    /** S-700 라이선스 정합성 - 라이선스별 구매수량 합계. [0]=license_id(UUID), [1]=합계(Long) */
    @Query("select r.license.id, sum(r.quantity) from LicensePurchaseRecord r group by r.license.id")
    List<Object[]> sumQuantityGroupByLicense();
}
