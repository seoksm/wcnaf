package com.winitech.smartAsset.domain.license;

import lombok.Getter;

import java.util.UUID;

@Getter
public class LicenseIncludedSoftwareInfo {

    private final UUID licenseIncludedSoftwareId;
    private final UUID softwareId;
    private final String softwareName;

    public LicenseIncludedSoftwareInfo(LicenseIncludedSoftware includedSoftware) {
        this.licenseIncludedSoftwareId = includedSoftware.getId();
        this.softwareId = includedSoftware.getSoftware().getId();
        this.softwareName = includedSoftware.getSoftware().getName();
    }
}
