package com.winitech.smartAsset.domain.license;

import java.util.List;
import java.util.UUID;

public interface LicenseAssignedUserReader {

    LicenseAssignedUser findById(UUID licenseAssignedUserId);

    List<LicenseAssignedUser> findAllByLicenseId(UUID licenseId);

    List<LicenseAssignedUser> findAllActiveByMemberId(UUID memberId);

    int countActiveByLicenseId(UUID licenseId);

    /** S-700 라이선스 정합성 - [0]=license_id(UUID), [1]=건수(Long) */
    List<Object[]> countActiveGroupByLicense();

    /** S-700 라이선스 정합성 - 미연결 배정 건수 */
    long countUnlinkedActive();
}
