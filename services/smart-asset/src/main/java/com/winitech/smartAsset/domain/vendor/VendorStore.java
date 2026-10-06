package com.winitech.smartAsset.domain.vendor;

import java.util.UUID;

public interface VendorStore {

    UUID store(Vendor vendor);

    void modify(Vendor vendor, VendorCommand.UpdateCommand updateCommand);

    void delete(Vendor vendor);
}
