package com.winitech.smartAsset.infrastructure.license;

import com.winitech.smartAsset.domain.license.License;
import com.winitech.smartAsset.domain.license.LicenseCommand;
import com.winitech.smartAsset.domain.license.LicenseStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LicenseStoreImpl implements LicenseStore {

    private final LicenseRepository licenseRepository;

    @Override
    public UUID store(License license) {
        return licenseRepository.save(license).getId();
    }

    @Override
    public void modify(License license, LicenseCommand.UpdateCommand updateCommand) {
        license.modify(updateCommand);
    }

    @Override
    public void delete(License license) {
        license.delete();
    }
}
