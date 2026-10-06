package com.winitech.smartAsset.infrastructure.software;

import com.winitech.smartAsset.domain.software.Software;
import com.winitech.smartAsset.domain.software.SoftwareCommand;
import com.winitech.smartAsset.domain.software.SoftwareStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SoftwareStoreImpl implements SoftwareStore {

    private final SoftwareRepository softwareRepository;

    @Override
    public UUID store(Software software) {
        return softwareRepository.save(software).getId();
    }

    @Override
    public void modify(Software software, SoftwareCommand.UpdateCommand updateCommand) {
        software.modify(updateCommand);
    }

    @Override
    public void delete(Software software) {
        software.delete();
    }
}
