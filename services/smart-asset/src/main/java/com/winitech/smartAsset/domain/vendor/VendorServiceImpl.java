package com.winitech.smartAsset.domain.vendor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VendorServiceImpl extends EgovAbstractServiceImpl implements VendorService {

    private final VendorReader vendorReader;
    private final VendorStore vendorStore;

    @Transactional
    @Override
    public UUID createVendor(VendorCommand command) {
        return vendorStore.store(command.toEntity());
    }

    @Transactional
    @Override
    public void updateVendor(VendorCommand.UpdateCommand updateCommand) {
        Vendor vendor = vendorReader.findById(updateCommand.getVendorId());
        vendorStore.modify(vendor, updateCommand);
    }

    @Transactional
    @Override
    public void deleteVendor(UUID vendorId) {
        Vendor vendor = vendorReader.findById(vendorId);
        vendorStore.delete(vendor);
    }

    @Override
    public List<VendorInfo> loadVendorList(String keyword) {
        return vendorReader.findAllByContainsKeyword(keyword).stream().map(VendorInfo::new).collect(Collectors.toList());
    }

    @Override
    public VendorInfo loadVendor(UUID vendorId) {
        return new VendorInfo(vendorReader.findById(vendorId));
    }
}
