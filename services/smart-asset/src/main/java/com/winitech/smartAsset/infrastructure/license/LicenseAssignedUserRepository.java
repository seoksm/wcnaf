package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.LicenseAssignedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LicenseAssignedUserRepository extends JpaRepository<LicenseAssignedUser, UUID> {

    List<LicenseAssignedUser> findAllByLicense_IdOrderByAssignedAtDesc(UUID licenseId);

    List<LicenseAssignedUser> findAllByMemberIdAndReleasedAtIsNullOrderByAssignedAtDesc(UUID memberId);

    @Query("select count(a) from LicenseAssignedUser a where a.license.id = :licenseId and a.releasedAt is null")
    int countActiveByLicenseId(UUID licenseId);

    /** S-700 라이선스 정합성 - 라이선스별 현재 배정 건수. [0]=license_id(UUID), [1]=건수(Long) */
    @Query("select a.license.id, count(a) from LicenseAssignedUser a where a.releasedAt is null group by a.license.id")
    List<Object[]> countActiveGroupByLicense();

    /** S-700 라이선스 정합성 - Q-47 "선배정 후구매"의 실제 원인인 미연결 배정 건수 */
    @Query("select count(a) from LicenseAssignedUser a where a.licensePurchaseRecord is null and a.releasedAt is null")
    long countUnlinkedActive();
}
