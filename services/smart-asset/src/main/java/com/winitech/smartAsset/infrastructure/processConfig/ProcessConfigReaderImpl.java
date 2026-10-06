package com.winitech.smartAsset.infrastructure.processConfig;

import com.winitech.smartAsset.domain.processConfig.ProcessConfig;
import com.winitech.smartAsset.domain.processConfig.ProcessConfigReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProcessConfigReaderImpl implements ProcessConfigReader {

    private final ProcessConfigRepository processConfigRepository;

    @Override
    public ProcessConfig getSingleton() {
        return processConfigRepository.findById(ProcessConfig.SINGLETON_ID).orElseThrow();
    }
}
