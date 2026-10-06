package com.winitech.smartAsset.infrastructure.vendor;

import com.winitech.smartAsset.domain.vendor.Vendor;
import com.winitech.smartAsset.domain.vendor.VendorReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VendorReaderImpl implements VendorReader {

    private final VendorRepository vendorRepository;

    @Override
    public Vendor findById(UUID vendorId) {
        return vendorRepository.findById(vendorId).orElseThrow();
    }

    @Override
    public List<Vendor> findAllByContainsKeyword(String keyword) {
        return vendorRepository.findAllByContainsKeyword(keyword);
    }
}
