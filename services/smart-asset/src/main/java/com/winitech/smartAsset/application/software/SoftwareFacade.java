package com.winitech.smartAsset.application.software;

import com.winitech.smartAsset.domain.software.SoftwareCommand;
import com.winitech.smartAsset.domain.software.SoftwareInfo;
import com.winitech.smartAsset.domain.software.SoftwareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoftwareFacade {

    private final SoftwareService softwareService;

    public UUID postSoftware(SoftwareCommand command) {
        return softwareService.createSoftware(command);
    }

    public void reviseSoftware(SoftwareCommand.UpdateCommand updateCommand) {
        softwareService.updateSoftware(updateCommand);
    }

    public void removeSoftware(UUID softwareId) {
        softwareService.deleteSoftware(softwareId);
    }

    public List<SoftwareInfo> getSoftwareList(String keyword) {
        return softwareService.loadSoftwareList(keyword);
    }

    public SoftwareInfo getSoftware(UUID softwareId) {
        return softwareService.loadSoftware(softwareId);
    }
}
