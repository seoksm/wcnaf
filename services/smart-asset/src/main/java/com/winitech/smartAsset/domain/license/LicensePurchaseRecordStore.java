package com.winitech.smartAsset.domain.license;

import com.winitech.smartAsset.domain.vendor.Vendor;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface LicensePurchaseRecordStore {

    LicensePurchaseRecord store(LicensePurchaseRecord record);

    void modify(LicensePurchaseRecord record, Vendor vendor, LocalDate purchaseDate, Integer quantity,
                BigDecimal unitPrice, BigDecimal totalAmount, String memo);
}
