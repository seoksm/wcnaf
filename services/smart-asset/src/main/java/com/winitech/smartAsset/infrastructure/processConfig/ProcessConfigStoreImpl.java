package com.winitech.smartAsset.infrastructure.processConfig;

import com.winitech.smartAsset.domain.processConfig.ProcessConfig;
import com.winitech.smartAsset.domain.processConfig.ProcessConfigCommand;
import com.winitech.smartAsset.domain.processConfig.ProcessConfigStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProcessConfigStoreImpl implements ProcessConfigStore {

    @Override
    public void modify(ProcessConfig processConfig, ProcessConfigCommand.UpdateCommand updateCommand) {
        processConfig.modify(updateCommand);
    }
}
