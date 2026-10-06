package com.winitech.smartAsset.domain.vendor;

import java.util.List;
import java.util.UUID;

public interface VendorService {

    UUID createVendor(VendorCommand command);

    void updateVendor(VendorCommand.UpdateCommand updateCommand);

    void deleteVendor(UUID vendorId);

    List<VendorInfo> loadVendorList(String keyword);

    VendorInfo loadVendor(UUID vendorId);
}
