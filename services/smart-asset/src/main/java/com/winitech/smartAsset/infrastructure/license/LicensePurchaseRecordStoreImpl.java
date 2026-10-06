package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.LicensePurchaseRecord;
import com.winitech.smartAsset.domain.license.LicensePurchaseRecordStore;
import com.winitech.smartAsset.domain.vendor.Vendor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LicensePurchaseRecordStoreImpl implements LicensePurchaseRecordStore {

    private final LicensePurchaseRecordRepository licensePurchaseRecordRepository;

    @Override
    public LicensePurchaseRecord store(LicensePurchaseRecord record) {
        return licensePurchaseRecordRepository.save(record);
    }

    @Override
    public void modify(LicensePurchaseRecord record, Vendor vendor, LocalDate purchaseDate, Integer quantity,
                        BigDecimal unitPrice, BigDecimal totalAmount, String memo) {
        record.modify(vendor, purchaseDate, quantity, unitPrice, totalAmount, memo);
    }
}
