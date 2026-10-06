package com.winitech.smartAsset.application.vendor;

import com.winitech.smartAsset.domain.vendor.VendorCommand;
import com.winitech.smartAsset.domain.vendor.VendorInfo;
import com.winitech.smartAsset.domain.vendor.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorFacade {

    private final VendorService vendorService;

    public UUID postVendor(VendorCommand command) {
        return vendorService.createVendor(command);
    }

    public void reviseVendor(VendorCommand.UpdateCommand updateCommand) {
        vendorService.updateVendor(updateCommand);
    }

    public void removeVendor(UUID vendorId) {
        vendorService.deleteVendor(vendorId);
    }

    public List<VendorInfo> getVendorList(String keyword) {
        return vendorService.loadVendorList(keyword);
    }

    public VendorInfo getVendor(UUID vendorId) {
        return vendorService.loadVendor(vendorId);
    }
}
