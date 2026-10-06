package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.LicenseAssignedUser;
import com.winitech.smartAsset.domain.license.LicenseAssignedUserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LicenseAssignedUserReaderImpl implements LicenseAssignedUserReader {

    private final LicenseAssignedUserRepository licenseAssignedUserRepository;

    @Override
    public LicenseAssignedUser findById(UUID licenseAssignedUserId) {
        return licenseAssignedUserRepository.findById(licenseAssignedUserId).orElseThrow();
    }

    @Override
    public List<LicenseAssignedUser> findAllByLicenseId(UUID licenseId) {
        return licenseAssignedUserRepository.findAllByLicense_IdOrderByAssignedAtDesc(licenseId);
    }

    @Override
    public List<LicenseAssignedUser> findAllActiveByMemberId(UUID memberId) {
        return licenseAssignedUserRepository.findAllByMemberIdAndReleasedAtIsNullOrderByAssignedAtDesc(memberId);
    }

    @Override
    public int countActiveByLicenseId(UUID licenseId) {
        return licenseAssignedUserRepository.countActiveByLicenseId(licenseId);
    }

    @Override
    public List<Object[]> countActiveGroupByLicense() {
        return licenseAssignedUserRepository.countActiveGroupByLicense();
    }

    @Override
    public long countUnlinkedActive() {
        return licenseAssignedUserRepository.countUnlinkedActive();
    }
}
