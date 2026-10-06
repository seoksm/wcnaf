package com.winitech.smartAsset.domain.license;

public interface LicenseAssignedUserStore {

    LicenseAssignedUser store(LicenseAssignedUser assignedUser);

    void requestRelease(LicenseAssignedUser assignedUser);

    void release(LicenseAssignedUser assignedUser);
}
