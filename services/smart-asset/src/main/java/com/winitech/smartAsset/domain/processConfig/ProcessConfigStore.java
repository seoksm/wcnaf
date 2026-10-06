package com.winitech.smartAsset.domain.processConfig;

public interface ProcessConfigStore {

    void modify(ProcessConfig processConfig, ProcessConfigCommand.UpdateCommand updateCommand);
}
