package com.winitech.smartAsset.domain.vendor;

import java.util.List;
import java.util.UUID;

public interface VendorReader {

    Vendor findById(UUID vendorId);

    List<Vendor> findAllByContainsKeyword(String keyword);
}
