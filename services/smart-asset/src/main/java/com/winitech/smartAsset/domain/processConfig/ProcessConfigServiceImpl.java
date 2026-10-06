package com.winitech.smartAsset.domain.processConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * C1(설계문서 §1) - 프로세스 설정은 전 화면이 매 요청 참조하는 전역 상태라 DB 조회로 두지 않고
 * 캐시한다. 변경 시(updateConfig) 즉시 무효화한다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProcessConfigServiceImpl extends EgovAbstractServiceImpl implements ProcessConfigService {

    public static final String CACHE_NAME = "processConfig";

    private final ProcessConfigReader processConfigReader;
    private final ProcessConfigStore processConfigStore;

    @Cacheable(CACHE_NAME)
    @Override
    public ProcessConfigInfo loadConfig() {
        return new ProcessConfigInfo(processConfigReader.getSingleton());
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Transactional
    @Override
    public void updateConfig(ProcessConfigCommand.UpdateCommand updateCommand) {
        ProcessConfig processConfig = processConfigReader.getSingleton();
        processConfigStore.modify(processConfig, updateCommand);
    }
}
