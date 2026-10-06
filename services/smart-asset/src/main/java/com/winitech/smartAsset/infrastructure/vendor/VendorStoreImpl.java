package com.winitech.smartAsset.infrastructure.vendor;

import com.winitech.smartAsset.domain.vendor.Vendor;
import com.winitech.smartAsset.domain.vendor.VendorCommand;
import com.winitech.smartAsset.domain.vendor.VendorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VendorStoreImpl implements VendorStore {

    private final VendorRepository vendorRepository;

    @Override
    public UUID store(Vendor vendor) {
        return vendorRepository.save(vendor).getId();
    }

    @Override
    public void modify(Vendor vendor, VendorCommand.UpdateCommand updateCommand) {
        vendor.modify(updateCommand);
    }

    @Override
    public void delete(Vendor vendor) {
        vendor.delete();
    }
}
