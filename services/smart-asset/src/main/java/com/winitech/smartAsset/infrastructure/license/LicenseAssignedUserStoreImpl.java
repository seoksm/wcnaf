package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.LicenseAssignedUser;
import com.winitech.smartAsset.domain.license.LicenseAssignedUserStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LicenseAssignedUserStoreImpl implements LicenseAssignedUserStore {

    private final LicenseAssignedUserRepository licenseAssignedUserRepository;

    @Override
    public LicenseAssignedUser store(LicenseAssignedUser assignedUser) {
        return licenseAssignedUserRepository.save(assignedUser);
    }

    @Override
    public void requestRelease(LicenseAssignedUser assignedUser) {
        assignedUser.requestRelease();
    }

    @Override
    public void release(LicenseAssignedUser assignedUser) {
        assignedUser.release();
    }
}
