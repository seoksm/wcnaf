package com.winitech.smartAsset.domain.license;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class LicensePurchaseRecordInfo {

    private final UUID licensePurchaseRecordId;
    private final UUID vendorId;
    private final String vendorName;
    private final LocalDate purchaseDate;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalAmount;
    private final String memo;

    public LicensePurchaseRecordInfo(LicensePurchaseRecord record) {
        this.licensePurchaseRecordId = record.getId();
        this.vendorId = record.getVendor() != null ? record.getVendor().getId() : null;
        this.vendorName = record.getVendor() != null ? record.getVendor().getName() : null;
        this.purchaseDate = record.getPurchaseDate();
        this.quantity = record.getQuantity();
        this.unitPrice = record.getUnitPrice();
        this.totalAmount = record.getTotalAmount();
        this.memo = record.getMemo();
    }
}
