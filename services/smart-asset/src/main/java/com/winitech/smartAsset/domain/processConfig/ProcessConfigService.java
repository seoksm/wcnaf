package com.winitech.smartAsset.domain.processConfig;

public interface ProcessConfigService {

    ProcessConfigInfo loadConfig();

    void updateConfig(ProcessConfigCommand.UpdateCommand updateCommand);
}
