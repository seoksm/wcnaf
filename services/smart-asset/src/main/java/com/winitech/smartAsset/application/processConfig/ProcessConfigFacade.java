package com.winitech.smartAsset.application.processConfig;

import com.winitech.smartAsset.domain.processConfig.ProcessConfigCommand;
import com.winitech.smartAsset.domain.processConfig.ProcessConfigInfo;
import com.winitech.smartAsset.domain.processConfig.ProcessConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessConfigFacade {

    private final ProcessConfigService processConfigService;

    public ProcessConfigInfo getConfig() {
        return processConfigService.loadConfig();
    }

    public void reviseConfig(ProcessConfigCommand.UpdateCommand updateCommand) {
        processConfigService.updateConfig(updateCommand);
    }
}
