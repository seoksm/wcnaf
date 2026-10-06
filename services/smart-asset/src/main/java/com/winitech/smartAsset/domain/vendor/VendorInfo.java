package com.winitech.smartAsset.domain.vendor;

import lombok.Getter;

import java.util.UUID;

@Getter
public class VendorInfo {

    private final UUID vendorId;
    private final String name;
    private final String contactName;
    private final String contactPhone;
    private final String contactEmail;
    private final String memo;
    private final Vendor.Status status;

    public VendorInfo(Vendor vendor) {
        this.vendorId = vendor.getId();
        this.name = vendor.getName();
        this.contactName = vendor.getContactName();
        this.contactPhone = vendor.getContactPhone();
        this.contactEmail = vendor.getContactEmail();
        this.memo = vendor.getMemo();
        this.status = vendor.getStatus();
    }
}
