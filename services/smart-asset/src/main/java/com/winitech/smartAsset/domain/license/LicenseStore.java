package com.winitech.smartAsset.domain.license;

import java.util.UUID;

public interface LicenseStore {

    UUID store(License license);

    void modify(License license, LicenseCommand.UpdateCommand updateCommand);

    void delete(License license);
}
